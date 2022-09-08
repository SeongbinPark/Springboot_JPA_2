package jpabook.jpashop.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")//테이블
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//기본생성자를 막아둬야함. ->
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")//db컬럼=테이블명_id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//앞이 클래스, 뒤가 필드(주문:멤버=다:일)
    @JoinColumn(name = "member_id")//매핑할 컬럼.(FK이름이 member_id가된다.
    private Member member;//여기에 값을 넣으면 member_id(FK) 값이 다른 멤버로 변경된다.

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Setter
    private LocalDateTime orderDate;//자바8부터 LocalDateTime

    @Setter
    @Enumerated(EnumType.STRING)//enum이면 넣어줘야됨, STRING 써야 순서상관X
    private OrderStatus status;//주문상태 enum으로 할거임.

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")//Order에서 FK 갖고 있다. 더 많이 access하기 때문.
    private Delivery delivery;


    /**
     * 연관관계 편의메서드 (양방향이면 연관관계 편의메서드 있는게 좋다.(양쪽 세팅을 메서드 하나로 해결))
     * JPA 에서는 다 쪽에서만 연관관계 설정 해주면 되지만
     * 객체의 입장에선 양방향 모두 연관관계 설정 해주어야한다. ( 객체 그래프 탐색 )
     */
    public void setMember(Member member) {

        this.member = member;
        member.getOrders().add(this);
        //this(order)의 필드에 멤버를 추가하고 그 order를 Member의 필드인 List<Order>에 추가한다.
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
        //onetoone 이므로 서로가 서로를 등록.
    }

    /**
     * 생성 메서드
     *///밖에서 set하는게 아니라 생성 할때부터 주문생성에대한 로직을 완성시켜둠. 앞으로 주문생성관련은 여기를 거치면됨.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);//ORDER상태로 강제
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /**
     * 주문 취소
     *///비지니스로직에 대한 체크로직이 엔티티내부에 있다.
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {//이미 배송완료되면 캔슬불가
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();//한번 주문 할 때 여러개 주문하면 각각에 캔슬날려줘야함.
        }
    }

    /**
     * 조회 로직
     */
    //전체 주문 가격 조회
    public int getTotalPrice() {
        //상품 각각의 가격 더하기.
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
