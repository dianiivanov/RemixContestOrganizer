package com.organizer.contest.remix.models;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;

import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String details;

    @NotNull
    private String rules;

    @NotNull
    private String prizes;

    @ManyToOne
    User owner;

    @OneToMany(mappedBy = "contest",cascade = CascadeType.ALL)
    private Set<Submission> submissions = new HashSet<>();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
