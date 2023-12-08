package com.wor.server.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Entity //엔티티 정의
@Table(name="vote_tbl") //사용하지 않으면 클래스 이름이 테이블 이름이 됨
@Getter //lombok getter
@Setter //lombok setter
public class voteEntity {
    @Id //기본키를 의미. 반드시 기본키를 가져야함.
    @Column(name="elec_num")
    private Integer elecNum;

    @NonNull
    @Column(name="area_num")
    private Integer areaNum;

    @NonNull
    @Column(name="end_time")
    private String endTime;

    @NonNull
    @Column(name="vote_name")
    private String voteName;

    @NonNull
    @Column(name="active")
    private Boolean active;

    @NonNull
    @Column(name="agree")
    private Integer agree;

    @NonNull
    @Column(name="disagree")
    private Integer disagree;

    @NonNull
    @Column(name="checked")
    private Boolean checked;
}
