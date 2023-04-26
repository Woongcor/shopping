package com.shopping.entity;

import com.shopping.constant.Role;
import com.shopping.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;


// DTO는 화면하고 연동된 것이라고 보면 됨, Entity는 데이터베이스와 연동된 것, jpa하고 연동된 것
@Entity
@Table(name = "members")
@Getter @Setter @ToString
public class Member extends BaseEntity{
    
    @Id  //기본키
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)  //숫자로 구분 아이디, 기본키 생성해주는
    private Long id;
    
    private String name;
    
    @Column(unique = true)  //유니크한 스타일, 절대로 똑같은 게 들어오면 안됨
    private String email;
    
    private String password;
    private String address;
    
    @Enumerated(EnumType.STRING)   // EnumType.ordinary (순서따지는 것), 실제로 사용하려면 String 이 편해서 string 사용
    private Role role;



    //화면에서 넘어오는 dto와 비번 암호화 객체를 이용하여 Member 엔터티 객체 생성하는 메소드
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();

        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());

        //password는 암호화시켜서 데이터베이스에 넣어야함, 암호화 시키는 객체가 passwordEncoder 이다
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        // member.setPassword(memberFormDto.getPassword());
        member.setRole(Role.USER);

        return member;
        
    }
}
