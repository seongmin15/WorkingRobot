package com.wor.server.service;

import com.wor.server.domain.entity.*;
import com.wor.server.domain.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class robotService {
    @Autowired
    private resRepository ResRepository;
    @Autowired
    private robotRepository RobotRepository;
    @Autowired
    private userResRepository UserResRepository;
    @Autowired
    private areaRepository AreaRepository;
    @Autowired
    private roomRepository RoomRepository;
    @Autowired
    private elecRepository ElecRepository;
    @Autowired
    private alarmService AlarmService;

    public List<Map<String, Object>> orderQueue = new ArrayList<>();

    private void setRobot(String serial)
    {
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        int queueNum = robot.get().getQueueNum();
        if (queueNum == 0 && robot.get().getProcess() == 0) return;
        else if (queueNum == 0 && robot.get().getProcess() == 1) {
            robot.get().setProcess(0);
            RobotRepository.save(robot.get());
            return;
        }
        robot.get().setQueueNum(queueNum - 1);
        robot.get().setProcess(0);
        RobotRepository.save(robot.get());

        if (queueNum == 1) {
            ResRepository.truncateSomethingTable();
        }
    }

    public void setOrder(String serial)
    {
        Map<String, Object> params = new HashMap<>();

        Optional<resEntity> res = ResRepository.findFirstByRobotSerialAndType(serial, "room");
        if (res.isEmpty()) {
            res = ResRepository.findFirstByRobotSerialAndType(serial, "elec");
            if (res.isEmpty()) {
                res = ResRepository.findFirstByRobotSerialAndType(serial, "trash");

                List<Optional<userResEntity>> userResList = UserResRepository.findAllByRobotSerial(serial);
                for (Optional<userResEntity> userRes : userResList) {
                    int queue_num = userRes.get().getQueueNum();
                    if (queue_num <= 0) {
                        UserResRepository.delete(userRes.get());

                        // 쓰레기 처리 종료 알람
                        AlarmService.save(userRes.get().getUserId(), "trash", false, "");
                        continue;
                    }
                    else if (queue_num == 1) {
                        // 쓰레기 처리 시작 알람
                        AlarmService.save(userRes.get().getUserId(), "trash", true, "");
                    }
                    userRes.get().setQueueNum(queue_num - 1);
                    UserResRepository.save(userRes.get());
                }

                if (res.isEmpty()) return;
            }
            else {
                params.put("on_off", res.get().getOnOff());
            }
        }
        else {
            params.put("on_off", res.get().getOnOff());
        }

        if (!res.get().getType().equals("trash")) {
            Optional<userResEntity> userRes = UserResRepository.findByRobotSerialAndQueueNum(serial, 0);

            if (userRes.isPresent()) {
                UserResRepository.delete(userRes.get());
            }
        }

        res.get().setProcess(1);
        ResRepository.save(res.get());

        params.put("robot_serial", serial);
        params.put("type", res.get().getType());
        params.put("coord", res.get().getCoord());

        this.orderQueue.add(params);
        log.info(this.orderQueue.toString());
    }

    public void processFinish(String serial)
    {
        setRobot(serial);
        setOrder(serial);
    }

    public List<Boolean> getElecList(String serial)
    {
        List<Boolean> ret = new ArrayList<>();

        Optional<areaEntity> area = AreaRepository.findByRobotSerial(serial);
        Integer area_num = area.get().getAreaNum();

        List<Optional<elecEntity>> elecList = ElecRepository.findAllByAreaNum(area_num);
        List<Optional<roomEntity>> roomList = RoomRepository.findAllByRobotSerial(serial);

        for (Optional<elecEntity> elec : elecList) {
            ret.add(elec.get().getOnoff());
        }

        for (Optional<roomEntity> room : roomList) {
            ret.add(room.get().getStatus());
        }

        return ret;
    }
}
