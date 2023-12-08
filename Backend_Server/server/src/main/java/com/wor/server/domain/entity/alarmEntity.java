package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity //엔티티 정의
@Table(name="alarm_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class alarmEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="alarm_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alarmNum;

    @NonNull
    @Column(name="user_id")
    private String userId;

    @NonNull
    @Column(name="type")
    private String type;

    @NonNull
    @Column(name="start_end")
    private Boolean startEnd;

    @NonNull
    @Column(name="date")
    private String date;

    @NonNull
    @Column(name="content")
    private String content;
}
