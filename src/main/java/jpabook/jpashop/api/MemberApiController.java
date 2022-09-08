package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    @PostMapping("/api/v1/members") // 회원 등록 API // @RequestBody는 JSON으로 온 req를 Member에 파싱해서 넣어줌.
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

    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest request) { // 보통 등록/수정은 API스펙이 다름. -> 별도의 DTO

        memberService.update(id, request.getName());
        Member findMember = memberService.findById(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    @Data
    static class UpdateMemberRequest {
        private String name;
    }


    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

    }


    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


}
