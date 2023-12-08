package com.wor.server.domain.dto;

import com.wor.server.domain.entity.roomResEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class roomResponseDto {
    private String message;
    private List<Optional<roomResEntity>> roomRes;
}
