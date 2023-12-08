package com.wor.server.domain.dto;

import com.wor.server.domain.entity.comEntity;
import com.wor.server.domain.entity.depEntity;
import com.wor.server.domain.entity.userEntity;
import com.wor.server.domain.repository.comRepository;
import com.wor.server.domain.repository.depRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
public class userInfo {
    private String userId;
    private String userName;
    private String robotSerial;
    private String comName;
    private Integer comNum;
    private String depName;
    private Integer depNum;
    private Integer areaNum;
    private Integer seatNum;
}
