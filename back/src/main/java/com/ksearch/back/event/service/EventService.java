package com.ksearch.back.event.service;

import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.event.dto.EventDto;
import com.ksearch.back.event.entity.Event;
import com.ksearch.back.event.repository.EventRepository;
import com.ksearch.back.event.repository.EventSearchRepository;
import com.ksearch.back.member.entity.Member;
import com.ksearch.back.member.repository.MemberRepository;
import com.ksearch.back.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.ksearch.back.error.type.AuthErrorCode.MemberNotFound;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventSearchRepository eventSearchRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    @Transactional
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
                            .rest(createEventRequestDto.getAmount())
                            .author(member)
                            .build()
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<EventDto.EventResponseDto> getAllEvents(
            Pageable pageable
    ) {
        return eventRepository.findAll(pageable)
                .map(EventDto.EventResponseDto::fromEntity);
    }

    public List<EventDto.EventResponseDto> searchEvent(String value) {
        return eventSearchRepository
                .findAllByTitleIsContainingOrDescriptionIsContaining(value, value)
                .stream().map(event -> EventDto.EventResponseDto.fromDocument(event))
                .collect(Collectors.toList());
    }

    public List<String> searchRecommendation(String value) {
        return eventSearchRepository
                .findAllByTitleIsContaining(value)
                .stream().map(event -> event.getTitle())
                .collect(Collectors.toList());
    }
}
