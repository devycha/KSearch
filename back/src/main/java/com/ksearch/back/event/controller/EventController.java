package com.ksearch.back.event.controller;

import com.ksearch.back.event.dto.EventDto.CreateEventRequestDto;
import com.ksearch.back.event.dto.EventDto.CreateEventResponseDto;
import com.ksearch.back.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/event")
    public String getEventHome(
            Pageable pageable,
            Model model
    ) {

        model.addAttribute("events", eventService.getAllEvents(pageable));
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
    public String getEventDetail(
            @PathVariable("eventId") Long eventId,
            Model model
    ) {
        model.addAttribute("event", eventService.getEventDetail(eventId));
        return "event/detail";
    }

    @ResponseBody
    @PostMapping("/event/{eventId}/participate")
    public boolean participateEvent(
            @PathVariable("eventId") Long eventId
    ) {
        return eventService.participateEvent(eventId);
    }

    @ResponseBody
    @GetMapping("/event/search/recommendation")
    public List<String> searchRecommendation(
            @RequestParam("q") String value
    ) {
        return eventService.searchRecommendation(value);
    }

    @GetMapping("/event/search")
    public String searchEvent(
            @RequestParam("q") String value,
            Model model
    ) {
        model.addAttribute("events", eventService.searchEvent(value));
        return "event/search-result";
    }

    @GetMapping("/event/search/rank")
    public String getEventSearchRank(
            Model model
    ) {
        model.addAttribute("map", eventService.getEventSearchRank());
        return "event/real-time";
    }
}
