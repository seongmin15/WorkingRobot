package com.wor.server.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity //엔티티 정의
@Table(name="dep_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class depEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="dep_num")
    private Integer depNum;

    @NonNull
    @Column(name="com_name")
    private String comName;

    @NonNull
    @Column(name="area_num")
    private Integer areaNum;

    @NonNull
    @Column(name="dep_name")
    private String depName;
}
