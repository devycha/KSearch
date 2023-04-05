package com.ksearch.back.member.service;

import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.error.type.AuthErrorCode;
import com.ksearch.back.member.dto.MemberDto;
import com.ksearch.back.member.entity.Member;
import com.ksearch.back.member.repository.MemberRepository;
import com.ksearch.back.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public MemberDto.CreateMemberResponseDto signUp(
            MemberDto.CreateMemberRequestDto createMemberRequestDto
    ) {
        return MemberDto.CreateMemberResponseDto.fromEntity(
                memberRepository.save(
                        Member.builder()
                                .name(createMemberRequestDto.getName())
                                .password(passwordEncoder.encode(
                                        createMemberRequestDto.getPassword())
                                )
                                .email(createMemberRequestDto.getEmail())
                                .build()
                )
        );
    }

    public String signIn(MemberDto.SignInDto signInDto) {
        Member member = this.authenticateByEmailAndPassword(signInDto.getEmail(), signInDto.getPassword());
        return jwtTokenUtil.generateToken(member.getEmail());
    }

    public Member authenticateByEmailAndPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if(!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.UserAuthNotMatch);
        }

        return member;
    }
}