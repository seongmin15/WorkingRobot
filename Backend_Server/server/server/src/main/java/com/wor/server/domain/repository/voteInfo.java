package com.wor.server.domain.repository;

public interface voteInfo {
    Integer getElecNum();
    Integer getAreaNum();
    String getEndTime();
    String getVoteName();
    Boolean getActive();
    Integer getAgree();
    Integer getDisagree();
}
