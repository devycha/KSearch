package com.ksearch.back.event.entity;

import com.ksearch.back.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(value = EntityListeners.class)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDate deadline;

    private Integer amount;

    private Integer rest;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    private Member author;
}
