package com.ksearch.back.event.service;

import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.error.exception.EventException;
import com.ksearch.back.error.type.EventErrorCode;
import com.ksearch.back.event.dto.EventDto;
import com.ksearch.back.event.entity.Event;
import com.ksearch.back.event.repository.EventRepository;
import com.ksearch.back.event.repository.EventSearchRepository;
import com.ksearch.back.member.entity.Member;
import com.ksearch.back.member.repository.MemberRepository;
import com.ksearch.back.security.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ksearch.back.error.type.AuthErrorCode.MemberNotFound;
import static com.ksearch.back.error.type.EventErrorCode.EventNotFound;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventSearchRepository eventSearchRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POST_VIEW_COUNT_PREFIX = "event:searchCount:";

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
        String key = POST_VIEW_COUNT_PREFIX + value;
        System.out.println("Value: " + redisTemplate.opsForValue().get(key));
        Integer viewCount = (Integer) redisTemplate.opsForValue().get(key);

        if (viewCount == null) {
            redisTemplate.opsForValue().set(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, viewCount + 1);
        }

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

    public Map<String, Integer> getEventSearchRank() {
        Set<String> keys = redisTemplate.keys("*" + "event:searchCount" + "*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("No search counts found");
            return new HashMap<>();
        }

        Map<String, Integer> hashMap = new HashMap<>();
        for (String key : keys) {
            hashMap.put(key.split(":")[2], Integer.parseInt(redisTemplate.opsForValue().get(key).toString()));
        }

        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(hashMap.entrySet());
        Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // 정렬된 Map 생성
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public EventDto.EventDetailResponseDto getEventDetail(Long eventId) {
        return EventDto.EventDetailResponseDto.fromEntity(
                eventRepository.findById(eventId).orElseThrow(
                        () -> new EventException(EventNotFound))
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean participateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventException(EventNotFound)
        );

        event.decrease();
        eventRepository.saveAndFlush(event);
        return true;
    }
}
