package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemQueryDto {

    @JsonIgnore //출력하기 싫어서 붙임.
    private  Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
}
