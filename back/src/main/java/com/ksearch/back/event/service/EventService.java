package com.ksearch.back.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksearch.back.error.exception.AuthException;
import com.ksearch.back.error.exception.EventException;
import com.ksearch.back.event.dto.ElasticSearchResponseDto;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<String> searchRecommendation(String value) throws JsonProcessingException {
        Set<String> recommendList = new HashSet<>();

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        /** 자동 완성 검색어 추천 */
        String url = "http://localhost:9200/ac_test/_search";
        String requestBody = "{\"query\": {\"match\": {\"name_ngram\": \"" + value + "\"}}}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
        String responseBody = restTemplate.postForEntity(url, entity, String.class).getBody();

        ElasticSearchResponseDto result = objectMapper.readValue(responseBody, ElasticSearchResponseDto.class);
        result.getHits().getHits().forEach(hit -> {
            recommendList.add(hit.get_source().getName());
        });

        /** 초성 검색 */
        url = "http://localhost:9200/chosung_test/_search";
        requestBody = "{\"query\": {\"match\": {\"name_chosung\": \"" + value + "\"}}}";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(requestBody, headers);
        responseBody = restTemplate.postForEntity(url, entity, String.class).getBody();

        result = objectMapper.readValue(responseBody, ElasticSearchResponseDto.class);
        result.getHits().getHits().forEach(hit -> {
            recommendList.add(hit.get_source().getName());
        });

        /** 한 -> 영 검색 */
        url = "http://localhost:9200/haneng_test/_search";
        requestBody = "{\"query\": {\"match\": {\"name_hantoeng\": \"" + value + "\"}}}";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(requestBody, headers);
        responseBody = restTemplate.postForEntity(url, entity, String.class).getBody();

        result = objectMapper.readValue(responseBody, ElasticSearchResponseDto.class);
        result.getHits().getHits().forEach(hit -> {
            recommendList.add(hit.get_source().getName());
        });

        /** 영 -> 한 검색 */
        url = "http://localhost:9200/haneng_test/_search";
        requestBody = "{\"query\": {\"match\": {\"name_engtohan\": \"" + value + "\"}}}";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(requestBody, headers);
        responseBody = restTemplate.postForEntity(url, entity, String.class).getBody();

        result = objectMapper.readValue(responseBody, ElasticSearchResponseDto.class);
        result.getHits().getHits().forEach(hit -> {
            recommendList.add(hit.get_source().getName());
        });

        System.out.println(recommendList);
        return new ArrayList<>(recommendList);
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
