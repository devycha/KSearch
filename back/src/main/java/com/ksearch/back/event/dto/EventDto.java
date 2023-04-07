package com.ksearch.back.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ksearch.back.event.entity.Event;
import com.ksearch.back.event.entity.EventDocument;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class EventDto {

    @Data
    @Builder
    public static class CreateEventRequestDto {
        public String title;
        public String description;
        public Integer amount;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" ,timezone = "Asia/Seoul")
        public LocalDate deadline;
    }

    @Data
    @Builder
    public static class CreateEventResponseDto {
        public boolean result;
        public Long id;
        public String title;
        public String deadline;

        public static CreateEventResponseDto fromEntity(Event event) {
            return CreateEventResponseDto.builder()
                    .id(event.getId())
                    .result(true)
                    .title(event.getTitle())
                    .deadline(event.getDeadline().toString())
                    .build();
        }
    }

    @Data
    @Builder
    public static class EventResponseDto {
        public String title;
        public String description;
        public LocalDate deadline;
        public Integer amount;
        public Integer rest;

        public static EventResponseDto fromEntity(Event event) {
            return EventResponseDto.builder()
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .deadline(event.getDeadline())
                    .amount(event.getAmount())
                    .rest(event.getRest())
                    .build();
        }

        public static EventResponseDto fromDocument(EventDocument event) {
            return EventResponseDto.builder()
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .deadline(event.getDeadline())
                    .amount(event.getAmount())
                    .rest(event.getRest())
                    .build();
        }
    }
}
