package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor//@AllArgsConstructor 와 다른점 : 이건 final필드로만 생성자만듦.
@Service
@Transactional(readOnly = true)//JPA의 모든 데이터변경은 트랜젝션 안에서 실행되어야한다.-> @Transactional 붙여줌.
//클래스에서 @Transactional 쓰면 public 메서드들은 트랜젝션에 걸려들어감.
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional// 만약 읽기가 아닌 쓰기에는 readOnly=true 붙이면 데이터 변경안됨....
    //이 떄 @Transactional 의 기본세팅은 readOnly=false . 클래스에 true여서 여기만 따로 붙여준다.
    public Long join(Member member) {
        validateDuplicateMember(member);//중복 회원 검증 추가.
        memberRepository.save(member);
        return member.getId();
        //persist하여 영속성컨텍스트에 갔을 때도 PK를 key로 사용하여 올림. ( 아직 DB는 가지도 않음 )
        // 그래서 getId해도 값이 있다는 것이 보장됨.
    }

    /**
     * 중복 회원 검증
     */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }//중복 있으면
    }

    /**
     * 전체 회원 조회
     *///@Transactional(readOnly = true) 궅이 안해도 클래스에 적어놓으면 자동 적용.
    //@Transactional(readOnly = true)//뒤 옵션 주면 JPA가 조회하는 곳에서는 성능최적화 ( 디테일 일단 넘어가자 )
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 멤버 하나 조회(Id로)
     *///@Transactional(readOnly = true) 궅이 안해도 클래스에 적어놓으면 자동 적용.
    //@Transactional(readOnly = true)//읽기(조회)에는 가급적 readOnly=true를 넣자.
    public Member findById(Long id) {
        return memberRepository.find(id);
    }

}
