package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Entity;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {//아이템은 JPA에 저장하기 전까지 Id 값이 없다.
        if (item.getId() == null) {//아이디가 없다= 완전 새로 생성한 객체이다.
            em.persist(item);//신규등록
        } else {//아이디 값이 있다. = 이미 DB에 등록된 걸 어디서 가져온 것.
            em.merge(item);//뒤에서 다시 설명(update비슷한것)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {//단건조회는 find쓰면 되지만 여러개조회는 jpql작성해야한다.
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
