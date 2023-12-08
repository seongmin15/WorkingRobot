package com.wor.server.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity //엔티티 정의
@Table(name="room_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class roomEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="room_num")
    private Integer roomNum;

    @NonNull
    @Column(name="room_name")
    private String roomName;

    @NonNull
    @Column(name="com_num")
    private Integer comNum;

    @NonNull
    @Column(name="robot_serial")
    private String robotSerial;

    @NonNull
    @Column(name="status")
    private Boolean status;

    @NonNull
    @Column(name="next")
    private Boolean next;
}
