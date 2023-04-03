package com.ksearch.back.member.service;

import com.ksearch.back.member.dto.MemberDto;
import com.ksearch.back.member.entity.Member;
import com.ksearch.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

}