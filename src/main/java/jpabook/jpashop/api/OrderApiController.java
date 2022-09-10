package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
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
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    //엔티티 그대로 노출
    @GetMapping("api/v1/orders")
    public Result ordersV1() {
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) { //루프돌면서 강제 초기화 (지연로딩 객체들 강제 로딩)
            order.getMember().getName(); //Member 강제 초기화
            order.getDelivery().getAddress(); //Delivery 강제 초기화

            //밑 두줄이 기존에서 추가됨.
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
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



    @Getter //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
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
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Getter //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
    static class OrderItemDto {
        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count;// 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName(); //depth 가 orderitem->item->name
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
