package jpabook.jpashop.service;

import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)//junit실행 할 때 스프링이랑같이 ( junit5에선 필요없음 )
@SpringBootTest
@Transactional//데이터를변경해야하기 떄문에 ,Rollback 기본값은 true -> commit안함.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    //Test라 그냥 편하게 필드주입
    @PersistenceContext
    EntityManager em;

    @Test
    //@Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member=new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);
        //then
        em.flush();
        assertEquals(member, memberRepository.find(savedId));


    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1=new Member();
        member1.setName("kim");
        Member member2=new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        memberService.join(member2);//여기서 예외가 튀어나와야한다.!!!!!(중간에 catch 없으므로)
        //then
        //Assert.fail() 은 코드가 여기 오면 테스트 실패라는 뜻
        fail("예외가 발생했어야한다.");
    }

}