package com.organizer.contest.remix.models;

import com.sun.istack.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class ContestApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 50)
    private String names;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String recordLabel;

    @NotNull
    @URL
    private String website;

    @NotNull
    private String artistName;

    @NotNull
    @URL
    private String songLink;

    @NotNull
    private String promotionInfo;

    @NotNull
    private String primaryGoal;

    @NotNull
    private Long remixesDesired;

    @NotNull
    private Boolean ownsCopyright;

    @NotNull
    private String investment;

    @NotNull
    private Boolean investInMarketing;

    @ManyToOne
    User owner;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();
}
