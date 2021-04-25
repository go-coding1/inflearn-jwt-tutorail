package com.inflearn.jwt.jwt.tutorail.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
/*
* @Size는 @Valid관련 어노테이션*/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull
    @Size(min=3, max=50)
    private String username;

    @NotNull
    @Size(min=3, max=100)
    private String password;
}
