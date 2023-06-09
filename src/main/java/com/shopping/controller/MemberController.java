package com.shopping.controller;

import com.shopping.dto.MemberFormDto;
import com.shopping.entity.Member;
import com.shopping.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(Model model){
        // 타임 리프에서 사용할 객체 memberFormDto을 바인딩합니다.
        model.addAttribute("memberFormDto", new MemberFormDto()) ;
        return  "/members/memberForm";
    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "/members/memberForm" ;
        }
        try {
            Member member = Member.createMember(memberFormDto,passwordEncoder);
            memberService.saveMember(member);

        } catch (IllegalStateException e){
            model.addAttribute("errMessage", e.getMessage());
            return "/members/memberForm" ;
        }

        System.out.println("포스트 방식 요청 들어옴");
        return  "redirect:/"; // 메인페이지로 가주세요.
    }

    @GetMapping("/login")
    public String loginMember(HttpServletRequest request, Model model){
        if(request.getParameterMap().containsKey("error")){
            model.addAttribute("loginErrorMsg","이메일 또는 비밀 번호를 확인해 주세요.");
            return "/members/memberLoginForm" ;
        }
        return "/members/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","이메일 또는 비밀 번호를 확인해 주세요.");
        return "/members/memberLoginForm";
    }
}