package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.wrapper.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) { //루프돌면서 강제 초기화 (지연로딩 객체들 강제 로딩)
            order.getMember().getName(); //Member 강제 초기화
            order.getDelivery().getAddress(); //Delivery 강제 초기화

            //밑 두줄이 기존에서 추가됨.
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
            //Order와 관련된 OrderItems 를 다 가져와서 forEach돌리면서 .getName()을 통해 강제 초기화
        }
        return all;
    }

    @GetMapping("api/v2/orders")
    public Result ordersV2() {
        List<Order> orders = orderRepository.findAllByJpql(new OrderSearch());
        List<OrderDto> orderDtos = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList()); //객체의 getter로 List로 만든다.

        return new Result(orderDtos);
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //값타입은 상관없음.
        private List<OrderItemDto> orderItems; //이런 엔티티를 DTO로 바꿔서 반환해야함.

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(o -> new OrderItemDto(o))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count;// 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName=orderItem.getItem().getName(); //depth 가 orderitem->item->name
            orderPrice=orderItem.getOrderPrice();
            count=orderItem.getCount();
        }
    }


}
