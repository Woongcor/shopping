package com.shopping.service;

import com.shopping.dto.MemberFormDto;
import com.shopping.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional  // 테스트가 종료되면, 자동으로 롤백하겠습니다.
public class MemberServiceTest {

    @Autowired  //memberserviceTest가  meberService를 의존하고 있어서 memberService가 지금 널 값인데 injection해서 값을 넣어주고 있음.
    MemberService memberService;

    @Autowired(required = false)  // 필수가 아니다
    PasswordEncoder passwordEncoder;


    public Member createMember(){
    //화면에서 여러분이 가입한 데이터
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail("qwert@naver.com");
        dto.setName("김기수");
        dto.setAddress("금천 가산동");
        dto.setPassword("1234");
        return Member.createMember(dto, passwordEncoder);

    }


    @Test
    @DisplayName("회원 가입 테스트")
    public void saveMember(){
        Member member = createMember();  //화면에 기입한 값
        
        // 실제 데이터 베이스에 들어간 값
        Member savedMember = memberService.saveMember(member);

        savedMember.setEmail("bbb@daum.net");

        Assertions.assertEquals(member.getEmail(), savedMember.getEmail());
        Assertions.assertEquals(member.getName(), savedMember.getName());

       // System.out.println("결과 확인");
       // System.out.println(savedMember.toString());

    }

}
