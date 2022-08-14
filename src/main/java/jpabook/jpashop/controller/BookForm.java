package jpabook.jpashop.controller;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {//이 강의는 Book만 사용하기로함.

    private Long id;//상품수정 할 때 필요함.

    private String name;
    private int price;
    private int stockQuantity;
    //여기 까지 상품의 공통속성

    private String author;
    private String isbn;
    //Book만의 속성
}
