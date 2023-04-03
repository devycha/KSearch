package com.ksearch.back.member.controller;

import com.ksearch.back.member.dto.MemberDto;
import com.ksearch.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public MemberDto.CreateMemberResponseDto signUp(
            MemberDto.CreateMemberRequestDto createMemberRequestDto
    ) {
        return memberService.signUp(createMemberRequestDto);
    }

//    @PostMapping("/auth/logIn")
//    public Http
}
