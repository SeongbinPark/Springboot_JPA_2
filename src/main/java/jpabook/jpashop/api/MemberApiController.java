package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.request.CreateMemberRequest;
import jpabook.jpashop.dto.request.UpdateMemberRequest;
import jpabook.jpashop.dto.response.CreateMemberResponse;
import jpabook.jpashop.dto.response.UpdateMemberResponse;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 조회 API
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers(); //@RestController에 @ResponseBody 있으므로 리스트가 JSON으로 변환된다.
    }

    @GetMapping("api/v2/members")
    public Result<List<MemberDto>> memberV2() {
        List<MemberDto> collect = memberService.findMembers().stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result<>(collect.size(), collect);
    }


    //wrapper/Result 도 만들어둠.
    @Data //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
    @AllArgsConstructor
    //컬렉션을 바로 반환하면 JSON 배열타입(전체가 [ ]로 감싸진 구조)으로 나가기 때문에 Result로 한 번 감싸서 반환. (배열로 나가면 유연성 별로)
    // -> 추후 요구사항 분명히 들어온다. -> JSON 구조 안깨지면서 추가하려면 한번 감싸줘야함.
    static class Result<T> { // 이때 T 는 List<MemberDto>
        private int count; // 이렇게 필드만 추가하면 간단히
        private T data; //이때 data는 List<MemberDto>
    }

    @Data //DTO의 getter는 JACKSON에 의해 객체->JSON (직렬화) 될 때 쓰인다.
    @AllArgsConstructor
    static class MemberDto {
        private String name; // 요구사항이 이름만 넘기는 걸로 하자.
    }


    /**
     * 회원 가입
     */
    @PostMapping("/api/v1/members") // 회원 등록 API // @RequestBody는 JSON으로 온 요청을 Member에 파싱해서 넣어줌.
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //물론 나중에 DTO로 바꿈

        Long id = memberService.join(member); // @Valid 어겼을 때 예외 처리 -> ControllerAdvice 사용.
        return new CreateMemberResponse(id);
    }

    @PostMapping("api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

//        Member member = Member.builder()
//                .id(request.getId())
//                .name(request.getName())
//                .build();
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정
     */
    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest request) { // 보통 등록/수정은 API스펙이 다름. -> 별도의 DTO

        memberService.update(id, request.getName());
        Member findMember = memberService.findById(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }













}
