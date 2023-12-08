package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Entity //엔티티 정의
@Table(name="room_res_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class roomResEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="res_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resNum;

    @NonNull
    @Column(name="user_id")
    private String userId;

    @NonNull
    @Column(name="room_num")
    private Integer roomNum;

    @NonNull
    @Column(name="date")
    private String date;

    @NonNull
    @Column(name="res_start")
    private String resStart;
}
