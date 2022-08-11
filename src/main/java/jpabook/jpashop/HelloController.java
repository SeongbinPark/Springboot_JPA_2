package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("Data", "hello!!!!");
        return "hello";//Model로 데이터를 뷰로 넘길수 있음. Map형식으로 "Data":"hello!!!!" 넘김.
        //return 은 화면이름.
    }
}
