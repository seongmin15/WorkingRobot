package com.wor.server.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class systemMessageResponseDto {
    private String message;
    private List<Map<String, Object>> message_list;
}
