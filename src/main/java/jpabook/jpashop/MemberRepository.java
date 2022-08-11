package jpabook.jpashop;


import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    EntityManager em;//스프링부트가 @persistenceContext 있으면 엔티티 매니저를 주입해줌.

    public Long save(Member member) {
        em.persist(member);
        return member.getId();//왜 member 반환X id만반환? ( 저장하면 웬만하면 리턴값 안만든다.( 아이디정도만))
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
