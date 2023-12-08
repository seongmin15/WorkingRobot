package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity //엔티티 정의
@Table(name="elec_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class elecEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="elec_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer elecNum;

    @Column(name="room_num")
    private Integer roomNum;

    @Column(name="area_num")
    private Integer areaNum;

    @NonNull
    @Column(name="elec_name")
    private String elecName;

    @NonNull
    @Column(name="coord")
    private String coord;

    @NonNull
    @Column(name="onoff")
    private Boolean onoff;

    @NonNull
    @Column(name="vote")
    private Boolean vote;
}
