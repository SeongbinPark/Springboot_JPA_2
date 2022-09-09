package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.scanner.ScannerImpl;

import java.time.LocalDateTime;
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

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화
            order.getDelivery().getOrder(); //LAZY 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        //주문 2개 // N = 2
        //N + 1문제 -> 1 + N(회원) + N(배송) -> 5개 쿼리가 나간다.
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());

        //이 루프가 2번 돈다. -> Member, Delivery의 LAZY초기화 두 번씩 일어나게된다.
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //고객정보아니고 배송지 정보

        //DTO가 파라미터로 엔티티를 받는건 문제 안됨. 중요하지않은 곳에서 중요한 엔티티의존하기 때문
        public SimpleOrderDto(Order order) {
            this.orderId=order.getId();
            this.name=order.getMember().getName(); // 여기서 LAZY 초기화 (LAZY객체 DB에서 땡겨옴(영속성컨텍스트에 있으면 그거 가져옴.))
            this.orderDate=order.getOrderDate();
            this.orderStatus=order.getStatus();
            this.address=order.getDelivery().getAddress(); // 여기서 LAZY 초기화 (LAZY객체 DB에서 땡겨옴(영속성컨텍스트에 있으면 그거 가져옴.))
        }
    }

}
