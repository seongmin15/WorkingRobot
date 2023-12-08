package com.wor.server.service;

import com.wor.server.domain.entity.resEntity;
import com.wor.server.domain.entity.robotEntity;
import com.wor.server.domain.entity.userResEntity;
import com.wor.server.domain.repository.resRepository;
import com.wor.server.domain.repository.robotRepository;
import com.wor.server.domain.repository.userResRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class reservationService {
    @Autowired
    private resRepository ResRepository;
    @Autowired
    private robotRepository RobotRepository;
    @Autowired
    private userResRepository UserResRepository;

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
                List<Optional<userResEntity>> userRes = UserResRepository.findAllByRobotSerial(serial);
                for (Optional<userResEntity> user : userRes) {
                    int queue_num = user.get().getQueueNum();
                    if (queue_num <= -1) {
                        UserResRepository.delete(user.get());
                        continue;
                    }
                    user.get().setQueueNum(queue_num - 1);
                    UserResRepository.save(user.get());
                }

                res = ResRepository.findFirstByRobotSerialAndType(serial, "trash");
                if (res.isEmpty()) return;
            }
            else {
                params.put("on_off", res.get().getOnOff());
            }
        }
        else {
            params.put("on_off", res.get().getOnOff());
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
}
