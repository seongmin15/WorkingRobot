package com.wor.server.domain.entity;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class seatId implements Serializable {
    private Integer seatNum;
    private Integer areaNum;
}
