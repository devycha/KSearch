package com.ksearch.back.member.controller;

import com.ksearch.back.member.dto.MemberDto;
import com.ksearch.back.member.service.MemberService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private Logger logger;

    @GetMapping("/member/signIn")
    public String getSignInPage() {
        System.out.println("Hello World");
        return "member/signin";
    }

    @PostMapping("/member/signIn")
    public String signIn(
            @ModelAttribute("signInDto") MemberDto.SignInDto signInDto,
            HttpServletResponse response,
            Model model
    ) {
        String token = memberService.signIn(signInDto);

        model.addAttribute("token", token);
        Cookie cookie = new Cookie("access_token", "Bearer+" + token);
        cookie.setMaxAge(60 * 60 * 24); // 쿠키 유효시간 설정 (예: 1일)
        cookie.setPath("/"); // 쿠키의 유효 경로 설정 (예: 전체 경로)
        response.addCookie(cookie); // 응답 헤더에 쿠키 추가
        return "index";
    }

    @GetMapping("/member/signUp")
    public String getSignUpPage() {
        return "member/signup";
    }

    @PostMapping("/member/signUp")
    public String signUp(
            @ModelAttribute("createMemberRequestDto")
            MemberDto.CreateMemberRequestDto createMemberRequestDto
    ) {
        memberService.signUp(createMemberRequestDto);
        return "redirect:/member/signup";
    }
}
