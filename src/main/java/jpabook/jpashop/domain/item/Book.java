package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity@Getter
@Setter
@DiscriminatorValue("B")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends Item {

    private String author;
    private String isbn;

}
