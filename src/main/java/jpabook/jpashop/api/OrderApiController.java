package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.wrapper.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * XToMany(Collections) 까지 모두 조회
 */

/**
 * V1. 엔티티 직접 노출
 * - 엔티티가 변하면 API 스펙이 변한다.
 * - 트랜잭션 안에서 지연 로딩 필요
 * - 양방향 연관관계 문제
 * <p>
 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
 * - 트랜잭션 안에서 지연 로딩 필요
 * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
 * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경가능)
 * <p>
 * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
 * - 페이징 가능
 * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
 * - 페이징 가능
 * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
 * - 페이징 불가능...
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("api/v1/orders")
    public Result ordersV1() {
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) { //루프돌면서 강제 초기화 (지연로딩 객체들 강제 로딩)
            order.getMember().getName(); //Member 강제 초기화
            order.getDelivery().getAddress(); //Delivery 강제 초기화

            //밑 두줄이 기존에서 추가됨.
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //item강제초기화
            //Order와 관련된 OrderItems 를 다 가져와서 forEach돌리면서 .getName()을 통해 강제 초기화
        }
        return new Result(all);
    }

    //Entity를 DTO로 변환
    @GetMapping("api/v2/orders")
    public Result ordersV2() {
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::new)
                .collect(toList());

        return new Result(orderDtos);
    }

    //fetch join 하면서 Entity를 DTO로 변환
    //member, delivery, orderitem, item 모두 페치조인 한 경우.
    @GetMapping("api/v3/orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem(); //Order를 Item과 같이 찾자.
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::new)
                .collect(toList());
        return new Result(orderDtos);
    }

    //컬렉션 페치조인 시 페이징 안되는 단점 보완
    //XToOne 만 페치조인(member, delivery) , 페이징
    @GetMapping("api/v3.1/orders")
    public Result ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDeliveryPage(offset, limit);
        //order, member, delivery 페치조인(지금 orderitem은 없는상태)
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::new)
                .collect(toList());
        return new Result(orderDtos);
    }

    //JPA에서 DTO바로 조회 (N + 1문제 터짐)
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    //JPA에서 DTO 바로 조회 (컬렉션 조회 최적화 - Map 사용)
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDtoOptimizatmion();
    }
}
