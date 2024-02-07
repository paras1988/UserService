package com.patti.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {

    private String token;
    private String userId;

}
