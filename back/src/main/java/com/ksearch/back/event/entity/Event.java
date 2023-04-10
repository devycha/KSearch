package com.ksearch.back.event.entity;

import com.ksearch.back.error.exception.EventException;
import com.ksearch.back.error.type.EventErrorCode;
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

    @ManyToOne(targetEntity = Member.class)
    private Member author;

    public void decrease() {
        if (this.rest == 0) {
            throw new EventException(EventErrorCode.OutOfStock);
        }
        this.rest -= 1;
    }
}
