package com.wor.server.service;

import com.wor.server.domain.entity.*;
import com.wor.server.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class trashService{
    @Autowired
    private seatRepository SeatRepository;
    @Autowired
    private areaRepository AreaRepository;
    @Autowired
    private resRepository ResRepository;
    @Autowired
    private robotRepository RobotRepository;
    @Autowired
    private userResRepository UserResRepository;
    @Autowired
    private robotService robotService;

    private String getCoord(seatId key) {
        Optional<seatEntity> seat = SeatRepository.findById(key);
        return seat.get().getCoord();
    }

    private String getSerial(Integer area_num) {
        Optional<areaEntity> area = AreaRepository.findById(area_num);
        return area.get().getRobotSerial();
    }

//    private String getTrashCoord(Integer area_num) {
//        Optional<areaEntity> area = AreaRepository.findById(area_num);
//        return area.get().getTrashCoord();
//    }

    private Integer getQueueNum(String serial) {
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        return robot.get().getQueueNum();
    }

    private Map<String, Object> makeOrder(Integer seat_num, Integer area_num)
    {
        seatId key = new seatId(seat_num, area_num);
        String coord = getCoord(key);
        String serial = getSerial(area_num);

        Map<String, Object> params = new HashMap<>();
        params.put("robot_serial", serial);
        params.put("type", "trash");
        params.put("coord", coord);

        return params;
    }

    private void putOrderInResTbl(Map<String, Object> params)
    {
        String serial = params.get("robot_serial").toString();
        String order = params.get("type").toString();
        String coord = params.get("coord").toString();

        resEntity res = new resEntity();
        res.setRobotSerial(serial);
        res.setType(order);
        res.setCoord(coord);
        res.setProcess(0);
        ResRepository.save(res);

        Integer queueNum = getQueueNum(serial);
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        if (robot.isPresent()) {
            robot.get().setQueueNum(queueNum+1);
            RobotRepository.save(robot.get());
        }
    }

    private void makeUserRes(String user_id, String robot_serial, Integer queue_num)
    {
        userResEntity userRes = new userResEntity();
        userRes.setUserId(user_id);
        userRes.setRobotSerial(robot_serial);
        userRes.setQueueNum(queue_num);
        UserResRepository.save(userRes);
    }

    public Integer resTrash(String user_id, Integer seat_num, Integer area_num)
    {
        Map<String, Object> params = makeOrder(seat_num, area_num);

        String serial = params.get("robot_serial").toString();

        Integer queue_num = getQueueNum(serial);

        putOrderInResTbl(params);

        if (queue_num == 0) {
            robotService.setOrder(serial);
        }

        makeUserRes(user_id, serial, queue_num);

        return queue_num;
    }

    public Integer getUserQueueNum(String user_id)
    {
        Optional<userResEntity> userRes = UserResRepository.findById(user_id);
        return userRes.map(userResEntity::getQueueNum).orElse(-1);
    }
}
