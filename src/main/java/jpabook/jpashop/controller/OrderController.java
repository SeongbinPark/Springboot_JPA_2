package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController { //고객과 상품 모두 선택해야하므로 디펜던시가 많다.

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")//주문 폼 출력
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")//실제 주문 받기.
    public String order(@RequestParam Long memberId,
                        @RequestParam Long itemId,
                        @RequestParam int count) {//OrderForm.html에서 각각 name이 넘어옴.

        orderService.order(memberId, itemId, count);//여기서 orderId를 받으면 주문결과창 등등으로 리다이렉트가능.
        return "redirect:/orders";//주문내역목록
    }

    @GetMapping("/orders")//@ModelAttribute 해놓으면 Model에 자동으로 담긴다.(이름 없으면 변수명.)
    public String orderList(@ModelAttribute OrderSearch orderSearch, Model model) {
        //model.addAttribute("orderSearch", orderSearch); @ModelAttribute 이 이 코드 만들어줌.
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "/order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
