package com.organizer.contest.remix.models;

import com.organizer.contest.remix.enums.Role;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Length(min = 3, max = 30)
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String artistName;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private Role role;

    @NotNull
    private Boolean active;

    @Column(length = 4096)
    private String biography;

    private String imageUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<Contest> contestsOwned = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.DETACH)
    private List<ContestApplication> contestApplicationsOwned = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Submission> submissions = new ArrayList<>();

    public User(@Length(min = 3, max = 30) String username, String password, String artistName, @Email String email, Role role) {
        this.username = username;
        this.password = password;
        this.artistName = artistName;
        this.email = email;
        this.role = role;
        this.active = true;
    }

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
