package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")//테이블
@Setter
@Getter
public class Order{

    @Id
    @GeneratedValue
    @Column(name = "orders_id")//db컬럼=테이블명_id
    private Long id;

    @ManyToOne//앞이 클래스, 뒤가 필드(주문:멤버=다:일)
    @JoinColumn(name = "member_id")//매핑할 컬럼.(FK이름이 member_id가된다.
    private Member member;//여기에 값을 넣으면 member_id(FK) 값이 다른 멤버로 변경된다.

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne//1:1은 FK 아무데나 둬도됨. 보통 Access많이하는 쪽에 둠.
    //여기서는 보통 Order를 가지고 Delivery를 찾을 일이 더 많으므로 여기에 FK
    //그러므로 FK와 가까운 Orders 를 연관관계주인으로 잡는다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;//자바8부터 LocalDateTime

    @Enumerated(EnumType.STRING)//enum이면 넣어줘야됨, STRING 써야 순서상관X
    private OrderStatus status;//주문상태 enum으로 할거임.

}
