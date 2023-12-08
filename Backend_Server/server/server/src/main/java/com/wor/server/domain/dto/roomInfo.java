package com.wor.server.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class roomInfo {
    private String message;
    private List<Map<String, Integer>> roomNames;
}
