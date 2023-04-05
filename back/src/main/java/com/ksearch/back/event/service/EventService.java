package com.ksearch.back.event.service;

import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.event.dto.EventDto;
import com.ksearch.back.event.entity.Event;
import com.ksearch.back.event.repository.EventRepository;
import com.ksearch.back.member.entity.Member;
import com.ksearch.back.member.repository.MemberRepository;
import com.ksearch.back.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.ksearch.back.error.type.AuthErrorCode.MemberNotFound;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    public EventDto.CreateEventResponseDto createEvent(
            HttpServletRequest request,
            EventDto.CreateEventRequestDto createEventRequestDto
    ) {
        String username = jwtTokenUtil.getUsernameFromCookie(request);
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new AuthException(MemberNotFound));

        return EventDto.CreateEventResponseDto.fromEntity(
                eventRepository.save(
                        Event.builder()
                            .title(createEventRequestDto.getTitle())
                            .description(createEventRequestDto.getDescription())
                            .amount(createEventRequestDto.getAmount())
                            .deadline(createEventRequestDto.getDeadline())
                            .author(member)
                            .build()
                )
        );
    }
}
