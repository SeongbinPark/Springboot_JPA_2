package jpabook.jpashop.service;


import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    /**
     * 주문 하기
     */
    @Transactional//주문은 데이터를 변경하는거기 때문에
    //주문하려면 멤버, 아이템, 몇개 주문할건지 필요.( 화면에 뜸 )
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티조회 ( memberId로 넘어왔으므로 멤버조회
        Member member = memberRepository.find(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성 ( 예제여서 회원에 있는 Address를 넣어준다. 실무라면 따론 입력한 배송지 넣어줌. )
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        //드디어 생성메서드를 쓴다.
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // 근데 누구는 여기서 OrderItem orderItem1= new OrderItem(); 으로 만들 수 있다.
        // 그런데 생성(필드추가, 로직추가)은 createOrderItem()을 통해서만 가능하게 해야한다. -> private생성자(protect까지됨.)



        //주문 생성(가장 중요)
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();//order의 식별자만 반환

        /**
         * 여기서 왜 order만 save(em.persist) 했냐.. 원래는 delivery, orderItem도 jpa에 넣어줘야(persist) 하는데
         .* Cascade 옵션 때문에 : Order를 persist 하면 orderItem에도 강제로 persist 날려준다
         * Delivery도 cascade옵션이라 따로 persist 안해줘도 된다.
         *
         * cascade 범위에 대한 고민 : order -> orderitem, order-> delivery 정도에서는 사용좋음.
         * ->   참조하는 주인이 private 오너일때만 사용: orderitem, delivery 는 order에서만 참조해서 씀.
         *        ( orderitem이 다른걸 참조할 순 있지만 orderitem을 참조하는 건 order밖에 없음.
         */


    }

    /**
     * 주문 취소
     */
    @Transactional//주문취소는 데이터를 변경하는거기 때문에
    public void cancelOrder(Long orderId) {//취소버튼 누르면 orderId만 넘어옴

        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();//Order엔티티 속에 있는 비지니스 로직.

    }


    //검색
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }


}
