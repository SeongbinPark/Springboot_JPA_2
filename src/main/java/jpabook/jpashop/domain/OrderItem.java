package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.util.Assert;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//막아둠.->생성메서드만 써라.
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")//다 쪽인 OrderItem을 연관관계주인으로.
    private Order order;

    private int orderPrice;//주문 가격

    private int count;//주문 수량

    @Builder
    public OrderItem(Item item, int orderPrice, int count) {
        Assert.notNull(item, "item must not be null");
        Assert.notNull(count,"count mush not be null");

        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    /**
     * 생성 메서드
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .count(count)
                .build();

        item.removeStock(count);//재고를 줄여줘야함.(넘어온 count 만큼)
        return orderItem;
    }


    /**
     * 비지니스 로직
     */
    //(주문취소한 여러개 상품 재고 증가)
    public void cancel() {
        this.item.addStock(count);
    }

    /**
     * 조회 로직
     */
    //주문상품 전체 가격조회
    public int getTotalPrice() {
        return this.getOrderPrice() * this.getCount();
    }
}
