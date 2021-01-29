package com.organizer.contest.remix.models;

import com.organizer.contest.remix.enums.Role;
import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Length(min = 3, max = 30)
    private String username;

    @NotNull
    @Length(min = 6, max = 30)
    private String password;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Role role;

    @NotNull
    private Boolean banned;

    private String description;

    private String imageUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private Set<Contest> contestsOwned = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private Set<ContestApplication> contestApplicationsOwned = new HashSet<>();

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
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
