package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")//폼을 열어본다.
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        //Model: 컨트롤러에서 뷰를 넘어갈때 map으로 데이터 싣어서 넘김.
        //여기선 MemberForm 객체를 넘겼으므로 화면에서 이 객체에 접근 할 수 있게됨.
        return "members/createMemberForm";//resources/templates/ 이후 경로.
    }

    //create에서 왜 Member(엔티티)로 안받고 MemberForm으로 받나? -> 차이가 크다.
    @PostMapping("/members/new")//실제 데이터를 등록한다.
    public String create(@Valid MemberForm form, BindingResult result) {//html의 MemberForm이 그대로 넘어옴.
        //만약 form에 오류가 있으면 팅겨버리는데(컨트롤러에 코드가 안넘어감) 근데
        //하지만 뭔가를 @Valid 하고 뒤에 BindingResult가 있으면
        // valid 실패시 오류가 result에 담겨서 컨트롤러코드가 실행이됨.

        if (result.hasErrors()) {//여기서의 에러는 Name을 입력 안했을 때만!!!!!!!!!!!!!
            return "members/createMemberForm";//스프링이 이 화면까지 result를 끌고간다.
            //그래서 어떤 에러가 있는지 이 화면에서 뿌릴 수 있다.
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setAddress(address);
        member.setName(form.getName());

        memberService.join(member);
        return "redirect:/";//등록을하고나면 재로딩되면 안좋아서 리다이렉트로 첫번째 페이지로 넘어감.
    }

    @GetMapping("/members") //전체 조회
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);//key, value
        // model에 지금 List가 들어가 있는데 이걸 html에서 루프를 돌면서 뿌리면 된다.
        return "members/memberList";//templates/members/memberList.html
    }
}
//엔티티를 최대한 순수하게 유지해야함. 오직 핵심 비즈니스로직에만 디펜던시가 있도록 설계.
//이래야 애플리케이션이 커져도 엔티티를 여러군데에서 유연하게 사용가능( 유지보수 용이 )
//화면에 대한 로직은 없어야함. -> 화면에 맞는 건 Form객체나 DTO를 써야함.
//DTO(data transfer object(getter, setter 만 있음)
