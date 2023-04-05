package com.ksearch.back.event.dto;

import com.ksearch.back.event.entity.Event;
import lombok.Builder;
import lombok.Data;

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
        public LocalDate deadline;
    }

    @Data
    @Builder
    public static class CreateEventResponseDto {
        public boolean result;
        public Long id;
        public String title;
        public LocalDate deadline;

        public static CreateEventResponseDto fromEntity(Event event) {
            return CreateEventResponseDto.builder()
                    .id(event.getId())
                    .result(true)
                    .title(event.getTitle())
                    .deadline(event.getDeadline())
                    .build();
        }
    }
}
