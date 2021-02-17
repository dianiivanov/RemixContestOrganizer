package com.organizer.contest.remix.models;

import com.organizer.contest.remix.enums.Genre;
import com.organizer.contest.remix.enums.Tonality;
import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
@Data
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer tempo;

    @Enumerated(EnumType.STRING)
    private Tonality tonality;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private String description;

    private String coverImageUrl;

    private String audioFileUrl;

    @ManyToOne
    private Contest contest;

    @ManyToOne
    private User owner;

    Long placeInContest;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
