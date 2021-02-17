package com.organizer.contest.remix.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;

import com.organizer.contest.remix.enums.ContestStatus;
import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(length = 4096)
    private String details;

    @NotBlank
    @Column(length = 4096)
    private String rules;

    @NotBlank
    @Column(length = 4096)
    private String prizes;

    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    private ContestStatus status;

    @OneToOne
    @JoinColumn(name = "contest_application_id", referencedColumnName = "id")
    private ContestApplication contestApplication;

    @ManyToOne
    private User owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stems_id", referencedColumnName = "id")
    private DBFile stems;

    @OneToMany(mappedBy = "contest",cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}