package jpabook.jpashop.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable//jpa의 내장타입( 어딘가 내장 될 수 있다.)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String city;

    private String street;

    private String zipcode;

    //최고는 Setter없이 생성자로만 초기화..But 이 때 오류.
    //왜냐 : JPA는 프록시, 리플렉션을 자주써서 항상 기본생성자 필요로 한다.
    //지금은 @Embeddable 붙여놔서 JPA가 관여함.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
