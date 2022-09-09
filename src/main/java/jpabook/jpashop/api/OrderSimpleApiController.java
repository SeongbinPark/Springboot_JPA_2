package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(컬렉션 관계(xToMnay)는 다음장에 설명 )
 * Order 먼저 조회
 * Order -> Member 연관 (ManyToOne)
 * Order -> Delivery 연관 (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    //엔티티를 그대로 API에 노출
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화
            order.getDelivery().getOrder(); //LAZY 강제 초기화
        }
        return all;
    }

    //엔티티를 DTO로 변환
    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2() {
        //주문 2개 // N = 2
        //N + 1문제 -> 1 + N(회원) + N(배송) -> 5개 쿼리가 나간다.
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());

        //이 루프가 2번 돈다. -> Member, Delivery의 LAZY초기화 두 번씩 일어나게된다.
        return orders.stream()
                .map(o ->new OrderSimpleQueryDto(o.getId(),o.getMember().getName(),o.getOrderDate(),o.getStatus(),o.getMember().getAddress()) )
                .collect(Collectors.toList());
    }

    //N+1 문제 해결을 위한 페치조인
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3() {
        List<Order> orders = orderRepository.findAllwithMemberDelivery();
        return orders.stream()
                .map(o ->new OrderSimpleQueryDto(o.getId(),o.getMember().getName(),o.getOrderDate(),o.getStatus(),o.getMember().getAddress()) )
                .collect(Collectors.toList());
    }

    // JPA에서 DTO로 바로 조회
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos(); // orderSimpleQueryRepository로 뺀 이유 : JPA에서 DTO 바로 뽑는 로직이 API에 의존적이어서 OrderRepository는 순수한 엔티티용도로 유지하고자 따로 빼놈
        //JPA에서 DTO로 바로 조회 했다.
    }



}
