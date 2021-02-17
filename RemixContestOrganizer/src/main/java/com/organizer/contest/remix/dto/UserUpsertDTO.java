package com.organizer.contest.remix.dto;

import com.organizer.contest.remix.enums.Role;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserUpsertDTO {
    Long id;
    @Size(min = 3, max = 30)
    @NotBlank
    String username;
    @Size(min = 3, max = 30)
    String password;
    @Size(max = 30)
    @NotBlank
    String artistName;
    @Email
    @NotBlank
    String email;
    @Size(max = 4096)
    String biography;
    String imageUrl;
    Role role;
}
