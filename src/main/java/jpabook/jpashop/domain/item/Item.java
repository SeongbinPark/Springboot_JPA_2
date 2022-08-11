package jpabook.jpashop.domain.item;


import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
//상속관계 전략을 부모 클래스에서 잡아줘야함
//우리는 싱글테이블 전략을 쓰니까 @Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")//dtype으로 종류구분.
public abstract class Item {//추상클래스로함. 구현체를 만들거기 때문에

    @Id
    @GeneratedValue
    @Column(name = "item_id")//칼럼명은 항상 소문자+_
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;//이 3개는 공통 속성 -> 상속해주자.

    @ManyToMany(mappedBy = "items")//반대편 필드명//서로 List라 다대다 관계이다.
    private List<Category> categories = new ArrayList<>();
}
