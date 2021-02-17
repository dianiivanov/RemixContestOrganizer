package com.organizer.contest.remix.models;

import com.organizer.contest.remix.enums.ApplicationStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
public class ContestApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String recordLabel;

    @URL
    private String website;

    @NotBlank
    @URL
    private String songLink;

    @NotBlank
    private String promotionInfo;

    @NotBlank
    private String primaryGoal;

    @NotBlank
    private String remixesDesired;

    @NotNull
    private Boolean ownsCopyright;

    @NotNull
    private String investment;

    @NotNull
    private Boolean investInMarketing;

    private ApplicationStatus status = ApplicationStatus.NEW;

    @OneToOne(mappedBy = "contestApplication")
    private Contest contest;

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