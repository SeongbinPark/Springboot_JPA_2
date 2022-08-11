package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Getter
@Embeddable//jpa의 내장타입( 어딘가 내장 될 수 있다.)
public class Address {

    private String city;

    private String street;

    private String zipcode;

}
