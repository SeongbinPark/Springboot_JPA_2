package jpabook.jpashop.dto;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
public class OrderItemDto {
    private String itemName; //상품명
    private int orderPrice; //주문 가격
    private int count;// 주문 수량

    public OrderItemDto(OrderItem orderItem) {
        itemName = orderItem.getItem()
                .getName(); //depth 가 orderitem->item->name
        orderPrice = orderItem.getOrderPrice();
        count = orderItem.getCount();
    }
}
