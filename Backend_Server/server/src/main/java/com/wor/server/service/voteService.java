package com.wor.server.service;

import com.wor.server.domain.entity.*;
import com.wor.server.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@EnableScheduling
public class voteService {
    @Autowired
    private voteRepository VoteRepository;
    @Autowired
    private elecRepository ElecRepository;
    @Autowired
    private areaRepository AreaRepository;
    @Autowired
    private resRepository ResRepository;
    @Autowired
    private robotRepository RobotRepository;
    @Autowired
    private robotService robotService;
    @Autowired
    private alarmService AlarmService;

    private Integer getQueueNum(String serial) {
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        return robot.get().getQueueNum();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void trunVote()
    {
        VoteRepository.truncateSomethingTable();
        List<elecEntity> elecList = ElecRepository.findAll();
        for (elecEntity elec : elecList) {
            if (elec.getRoomNum() == null) {
                elec.setVote(false);
                ElecRepository.save(elec);
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void checkVote() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        LocalTime date = LocalTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String now = date.format(dateFormat);


        List<Optional<voteEntity>> voteList = VoteRepository.findAllByChecked(false);

        for (Optional<voteEntity> vote : voteList) {
            Date com1 = new Date(simpleDateFormat.parse(now).getTime());
            Date com2 = new Date(simpleDateFormat.parse(vote.get().getEndTime()).getTime());

            Integer result = com2.compareTo(com1);

            if (result <= 0) {
                log.info("result:" + result);
                vote.get().setChecked(true);
                VoteRepository.save(vote.get());
                makeRes(vote.get().getElecNum(), vote.get().getAreaNum());
            }
        }
    }

    public void makeRes(Integer elec_num, Integer area_num) {
        Optional<areaEntity> area = AreaRepository.findById(area_num);
        Optional<elecEntity> elec = ElecRepository.findById(elec_num);
        Optional<voteEntity> vote = VoteRepository.findById(elec_num);

        String serial = area.get().getRobotSerial();
        String onoff;
        if (vote.get().getAgree() - vote.get().getDisagree() > 0) {
            onoff = "On";
            elec.get().setOnoff(true);
        }
        else if (vote.get().getAgree() - vote.get().getDisagree() == 0)
        {
            vote.get().setActive(false);
            VoteRepository.save(vote.get());

            elec.get().setVote(false);
            ElecRepository.save(elec.get());
            return;
        }
        else {
            onoff = "Off";
            elec.get().setOnoff(false);
        }

        resEntity res = new resEntity();
        res.setRobotSerial(serial);
        res.setType("elec");
        res.setCoord(elec.get().getCoord());
        res.setProcess(0);
        res.setOnOff(onoff);
        ResRepository.save(res);

        vote.get().setActive(false);
        VoteRepository.save(vote.get());

        elec.get().setVote(false);
        ElecRepository.save(elec.get());

        Integer queueNum = getQueueNum(serial);
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        if (robot.isPresent()) {
            robot.get().setQueueNum(queueNum+1);
            RobotRepository.save(robot.get());
        }

        if (queueNum == 0) robotService.setOrder(serial);

        // 투표 종료 알람
        AlarmService.save(vote.get().getUserId(), "vote", false, elec.get().getElecName());
    }

    public List<Map<String, Object>> getElecInfo(Integer area_num)
    {
        List<Map<String, Object>> ret = new ArrayList<>();
        List<Optional<elecEntity>> elecList = ElecRepository.findAllByAreaNum(area_num);

        for (Optional<elecEntity> elec : elecList) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(elec.get().getElecName(), elec.get().getElecNum());
            if (!elec.get().getVote()) ret.add(tmp);
        }

        return ret;
    }

    public List<voteInfo> getVoteInfo(Integer area_num)
    {
        List<voteInfo> ret = new ArrayList<>();
        List<Optional<voteInfo>> voteList = VoteRepository.findAllByAreaNum(area_num);

        for (Optional<voteInfo> vote : voteList) {
            ret.add(vote.get());
        }

        return ret;
    }

    public void createVote(Integer elec_num, Integer area_num, String title, String user_id) {
        Optional<voteEntity> vote = VoteRepository.findById(elec_num);
        LocalTime now = LocalTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        now = now.plusMinutes(1);
        String endTime = now.format(dateFormat);

        if (vote.isEmpty()) {
            voteEntity new_vote = new voteEntity();
            new_vote.setElecNum(elec_num);
            new_vote.setAreaNum(area_num);
            new_vote.setEndTime(endTime);
            new_vote.setVoteName(title);
            new_vote.setActive(true);
            new_vote.setAgree(0);
            new_vote.setDisagree(0);
            new_vote.setChecked(false);
            new_vote.setUserId(user_id);
            VoteRepository.save(new_vote);
        }
        else {
            vote.get().setEndTime(endTime);
            vote.get().setVoteName(title);
            vote.get().setActive(true);
            vote.get().setAgree(0);
            vote.get().setDisagree(0);
            vote.get().setChecked(false);
            vote.get().setUserId(user_id);
            VoteRepository.save(vote.get());
        }

        Optional<elecEntity> elec = ElecRepository.findById(elec_num);
        elec.get().setVote(true);
        ElecRepository.save(elec.get());

        // 투표 시작 알람
        AlarmService.save(user_id, "vote", true, elec.get().getElecName());
    }

    public void vote(Integer elec_num, Boolean agree) {
        Optional<voteEntity> vote = VoteRepository.findById(elec_num);

        if (agree) vote.get().setAgree(vote.get().getAgree() + 1);
        else vote.get().setDisagree(vote.get().getDisagree() + 1);

        VoteRepository.save(vote.get());
    }
}
