package com.wor.server.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class roomTimeInfo {
    private String message;
    private List<Integer> times;
}
