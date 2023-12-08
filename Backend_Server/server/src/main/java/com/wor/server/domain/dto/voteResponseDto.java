package com.wor.server.domain.dto;

import com.wor.server.domain.repository.voteInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class voteResponseDto {
    private String message;
    private List<voteInfo> vote_list;
}
