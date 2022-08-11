package jpabook.jpashop.domain;


import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany//서로 List로 가지므로 ManytoMany
    @JoinTable(name = "category_item",//중간테이블 매핑..객체는 컬렉션:컬렉션이라 다대다 가능한데
            //RDB는 컬렉션관계를 양쪽에 가질 수 있는게 아니라 일대다, 다대일 테이블로 풀어야함.
        joinColumns = @JoinColumn(name = "category_id"),//중간테이블의 category_id
        inverseJoinColumns = @JoinColumn(name = "item_id"))//중간테이블의 item_id
    private List<Item> items = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;//Category는 계층구조이므로 부모, 자식 알아야함.

    //자식은 여러명
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

        //같은 엔티티에 대해서 셀프로 양방향연관관계 걸음.
}
