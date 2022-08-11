package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext//이게 있으면 스프링이 생성한 JPA의  entitymanager를 주입해줌.
    private EntityManager em;//스프링부트가 @persistenceContext 있으면 엔티티 매니저를 주입해줌.


    public Long save(Member member) {
        em.persist(member);//여기서 JPA가 얘를 저장함.
        return member.getId();
    }

    public Member find(Long id) {
        //Member 중에 id를 이용하여 찾아라.
        return em.find(Member.class, id);
    }
    //*********************************************************************************
    //1. persist하면 영속성컨텍스트에 일단 member엔티티를 넣고 나중에 트랜젝션이 commit되는 시점에
    //   DB에 반영 (INSERT 쿼리가 날라감)
    //2. find메서드는 (단건조회) 는 (찾을타입, PK) 넣어주면 됨.
    //*********************************************************************************


    public List<Member> findAll() {
        //em.createQuery(jpql, 찾아야되는 타입).getResultList() ( 멤버를 리스트로 만들어줌 )
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();//jpql : sql과 거의 비슷 -> 결국 sql로 번역되어야하기 때문에
        //*********but sql: 테이블 대상으로 쿼리, jpql : 엔티티객체를 대상으로 쿼리
        //엔티티 멤버 조회.
    }

    public List<Member> findByName(String name) {//*********where문 추가. ":name" 은 파라미터 바인딩
        return em.createQuery("select m from Member m where m.name= :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }


}
