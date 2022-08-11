package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne//delivery: order = 1:1
    private Order order;

    @Embedded//내장타입이기 때문에
    private Address address;

    @Enumerated(EnumType.STRING)//enum이면 넣어줘야됨, STRING 써야 순서상관X
    private DeliveryStatus status;//READY, COMP





}
