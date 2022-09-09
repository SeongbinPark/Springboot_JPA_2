package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; //고객정보아니고 배송지 정보

    //DTO가 파라미터로 엔티티를 받는건 문제 안됨. 중요하지않은 곳에서 중요한 엔티티의존하기 때문
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name; // 여기서 LAZY 초기화 (LAZY객체 DB에서 땡겨옴(영속성컨텍스트에 있으면 그거 가져옴.))
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address; // 여기서 LAZY 초기화 (LAZY객체 DB에서 땡겨옴(영속성컨텍스트에 있으면 그거 가져옴.))
    }
}
