package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity //엔티티 정의
@Table(name="seat_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
@IdClass(seatId.class)
public class seatEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="seat_num")
    private Integer seatNum;

    @Id
    @Column(name="area_num")
    private Integer areaNum;

    @NonNull
    @Column(name="coord")
    private String coord;

    @NonNull
    @Column(name="fill")
    private Boolean fill;
}
