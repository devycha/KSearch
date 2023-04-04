package com.ksearch.back.member.dto;

import com.ksearch.back.member.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

public class MemberDto {

    @Data
    @Builder
    @ToString
    public static class CreateMemberRequestDto {
        private String name;
        private String email;
        private String password;
    }

    @Data
    @Builder
    public static class CreateMemberResponseDto {
        private boolean result;
        private String name;

        public static CreateMemberResponseDto fromEntity(Member member) {
            return CreateMemberResponseDto.builder()
                    .result(true)
                    .name(member.getName())
                    .build();
        }

    }

    @Data
    @Builder
    public static class SignInDto {
        private String email;
        private String password;
    }
}
