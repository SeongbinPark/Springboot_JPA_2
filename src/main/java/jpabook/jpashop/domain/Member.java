package jpabook.jpashop.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")//칼럼 이름 지정.
    private Long id;

    @NotNull
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")//Member:List<Order>=one:many
    //orders 테이블에 있는 member 필드에 의해 난 매핑된거야.
    //==난 읽기전용이다., 난 매핑된 거울일 뿐이야.
    private List<Order> orders = new ArrayList<>();
    //여기에 무슨 값을 넣는다 해서 저 FK값이 변경되지 않는다.
}
