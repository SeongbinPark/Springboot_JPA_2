package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByJpql(new OrderSearch());
        for (Order order : all) { //루프돌면서 강제 초기화 (지연로딩 객체들 강제 로딩)
            order.getMember().getName();
            order.getDelivery().getAddress();

            //밑 두줄이 기존에서 추가됨.
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
            //Order와 관련된 OrderItems 를 다 가져와서 forEach돌리면서 .getName()을 통해 강제 초기화
        }
        return all;
    }
}
