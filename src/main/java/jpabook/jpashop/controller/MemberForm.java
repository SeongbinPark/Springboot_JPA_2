package jpabook.jpashop.controller;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class MemberForm { //컨트롤러계층에 엔티티 노출 안하기 위함.

    @NotEmpty(message = "회원 이름은 필수입니다.")//이름 입력 안했을 때 메시지
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
