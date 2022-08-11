package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


//Junit4
@RunWith(SpringRunner.class)//나 스프링관련 된 걸로 테스트 할거야라고 junit에 알려줌
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)//커밋해준다.
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());//저장한멤버vs조회한멤버
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());//저장한멤버vs조회한멤버
        assertThat(findMember).isSameAs(member);//True
        //findmember == member
    }


}
