package jpabook.jpashop.dto;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import jpabook.jpashop.dto.OrderItemDto;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
public class OrderDto {

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
