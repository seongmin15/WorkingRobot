package com.wor.server.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class userResponseDto {
    private String message;
    private userInfo UserInfo;
}
