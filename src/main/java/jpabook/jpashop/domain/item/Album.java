package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity@Getter
@Setter
@DiscriminatorValue("A")//single table이라 DB에 저장될 때 구분이 되야해서(디폴트가 맨앞글자)
public class Album extends Item {

    private String artist;
    private String etc;
}
