package com.organizer.contest.remix.dto;

import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserProfileInfoDTO {
    Long id;
    @Size(max = 30)
    @NotBlank
    String artistName;
    @Email
    @NotBlank
    String email;
    @Size(max = 4096)
    String biography;
    String imageUrl;
}
