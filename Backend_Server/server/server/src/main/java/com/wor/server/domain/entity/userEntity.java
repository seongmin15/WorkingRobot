package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Entity //엔티티 정의
@Table(name="user_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class userEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="user_id")
    private String userId;

    @NonNull
    @Column(name="user_pw")
    private String userPw;

    @NonNull
    @Column(name="user_name")
    private String userName;

    @NonNull
    @Column(name="com_num")
    private Integer comNum;

    @NonNull
    @Column(name="dep_num")
    private Integer depNum;

    @NonNull
    @Column(name="area_num")
    private Integer areaNum;

    @NonNull
    @Column(name="seat_num")
    private Integer seatNum;

    @NonNull
    @Column(name="robot_serial")
    private String robotSerial;
}
