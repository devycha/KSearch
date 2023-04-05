package com.ksearch.back.event.controller;

import com.ksearch.back.event.dto.EventDto;
import com.ksearch.back.event.dto.EventDto.CreateEventRequestDto;
import com.ksearch.back.event.dto.EventDto.CreateEventResponseDto;
import com.ksearch.back.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/event")
    public String getEventHome() {
        return "event/home";
    }

    @GetMapping("/event/new")
    public String getEventCreationPage() {
        return "event/new";
    }

    @PostMapping("/event/new")
    public String createNewEvent(
            HttpServletRequest request,
            CreateEventRequestDto createEventRequestDto
    ) {
        CreateEventResponseDto event = eventService.createEvent(request, createEventRequestDto);

        return String.format("redirect:/event/{%d}/detail", event.getId());
    }

    @GetMapping("/event/{eventId}/detail")
    public String getEventDetail() {
        return "event/detail";
    }
}
