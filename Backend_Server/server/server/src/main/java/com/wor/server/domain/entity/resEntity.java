package com.wor.server.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Entity //엔티티 정의
@Table(name="res_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class resEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="res_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resNum;

    @NonNull
    @Column(name="robot_serial")
    private String robotSerial;

    @NonNull
    @Column(name="type")
    private String type;

    @NonNull
    @Column(name="coord")
    private String coord;

    @NonNull
    @Column(name="process")
    private Integer process;

    @Column(name="on_off")
    private String onOff;
}
