package com.wor.server.service;

import com.wor.server.domain.entity.*;
import com.wor.server.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
public class roomService {
    @Autowired
    private roomResRepository RoomResRepository;
    @Autowired
    private roomRepository RoomRepository;
    @Autowired
    private resRepository ResRepository;
    @Autowired
    private robotRepository RobotRepository;
    @Autowired
    private elecRepository ElecRepository;
    @Autowired
    private reservationService ReservationService;

    private Integer timeToInteger(String time)
    {
        String[] tmp = time.split(":");
        String hour = tmp[0];

        return Integer.parseInt(hour) - 9;
    }

    private String getSerial(Integer room_num) {
        Optional<roomEntity> room = RoomRepository.findById(room_num);
        return room.get().getRobotSerial();
    }

    private Integer getQueueNum(String serial) {
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        return robot.get().getQueueNum();
    }

    private String getCoord(Integer room_num) {
        String ret = "";
        List<Optional<elecEntity>> elecList = ElecRepository.findAllByRoomNum(room_num);

        for (Optional<elecEntity> elec : elecList) {
            String coord = elec.get().getCoord();
            ret = ret + coord;
        }

        return ret;
    }

    private void roomOn(Integer room_num)
    {
        List<Optional<elecEntity>> elecList = ElecRepository.findAllByRoomNum(room_num);

        for (Optional<elecEntity> elec : elecList) {
            elec.get().setOnoff(true);
            ElecRepository.save(elec.get());
        }
    }

    private void roomOff(Integer room_num)
    {
        List<Optional<elecEntity>> elecList = ElecRepository.findAllByRoomNum(room_num);

        for (Optional<elecEntity> elec : elecList) {
            elec.get().setOnoff(false);
            ElecRepository.save(elec.get());
        }
    }

    private void resetNext()
    {
        List<roomEntity> roomList = RoomRepository.findAll();

        for (roomEntity room : roomList) {
            room.setNext(false);
            RoomRepository.save(room);
        }
    }

    private List<Optional<roomResEntity>> checkResStart()
    {
        LocalTime now = LocalTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        DateTimeFormatter dateFormat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("HH");

        String today = now.format(dateFormat1);
        Integer tmp_hour = Integer.parseInt(now.format(dateFormat2)) + 1;

        String hour = tmp_hour.toString();
        hour = hour + ":00:00";

        return RoomResRepository.findAllByDateAndResStart(today, hour);
    }

    private void putOrderInResTbl(String serial, String coord, String onoff)
    {
        resEntity res = new resEntity();
        res.setRobotSerial(serial);
        res.setType("room");
        res.setCoord(coord);
        res.setProcess(0);
        res.setOnOff(onoff);
        ResRepository.save(res);

        Integer queueNum = getQueueNum(serial);
        Optional<robotEntity> robot = RobotRepository.findById(serial);
        if (robot.isPresent()) {
            robot.get().setQueueNum(queueNum+1);
            RobotRepository.save(robot.get());
        }

        if (queueNum == 0) ReservationService.setOrder(serial);
    }

    @Scheduled(cron = "0 50 8-19 * * *", zone = "Asia/Seoul")
    public void putOrderInResTblOn()
    {
        resetNext();

        List<Optional<roomResEntity>> roomResList = checkResStart();

        if (roomResList == null) return;

        for (Optional<roomResEntity> roomRes : roomResList) {
            Optional<roomEntity> roomStatus = RoomRepository.findById(roomRes.get().getRoomNum());
            roomStatus.get().setNext(true);
            if (roomStatus.get().getStatus()) {
                RoomRepository.save(roomStatus.get());
                continue;
            }
            roomStatus.get().setStatus(true);
            RoomRepository.save(roomStatus.get());

            String serial = getSerial(roomRes.get().getRoomNum());
            String coord = getCoord(roomRes.get().getRoomNum());

            roomOn(roomRes.get().getRoomNum());

            putOrderInResTbl(serial, coord, "On");

            RoomResRepository.delete(roomRes.get());
        }
    }

    @Scheduled(cron = "0 10 9-20 * * *", zone = "Asia/Seoul")
    public void putOrderInResTblOff()
    {
        List<roomEntity> roomList = RoomRepository.findAll();

        for (roomEntity room : roomList) {
            String coord = getCoord(room.getRoomNum());
            if (!room.getNext() && room.getStatus()) {
                room.setStatus(false);
                roomOff(room.getRoomNum());
                putOrderInResTbl(room.getRobotSerial(), coord, "Off");
            }
            RoomRepository.save(room);
        }
    }

    public List<Map<String, Integer>> getRoomNames(Integer com_num)
    {
        List<Map<String, Integer>> ret = new ArrayList<>();
        List<Optional<roomEntity>> rooms = RoomRepository.findAllByComNum(com_num);
        for (Optional<roomEntity> room : rooms) {
            Map<String, Integer> roomInfo = new HashMap<>();
            String roomName = room.get().getRoomName();
            Integer roomNum = room.get().getRoomNum();
            roomInfo.put(roomName, roomNum);
            ret.add(roomInfo);
        }

        return ret;
    }

    public List<Optional<roomResEntity>> getUserRoomRes(String user_id)
    {
        return RoomResRepository.findAllByUserId(user_id, Sort.by(Sort.Direction.ASC, "date", "resStart"));
    }

    public List<Integer> getRoomResTime(String date, Integer room_num)
    {
        List<Integer> ret = new ArrayList<>();
        List<Optional<roomResEntity>> roomResTime = RoomResRepository.findAllByDateAndRoomNum(date, room_num);

        LocalTime now = LocalTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        DateTimeFormatter dateFormat1 = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("mm");

        Integer hour = Integer.parseInt(now.format(dateFormat1)) - 8;
        Integer minute = Integer.parseInt(now.format(dateFormat2));

        for (Optional<roomResEntity> roomRes : roomResTime) {
            String time = roomRes.get().getResStart();
            Integer time_num = timeToInteger(time);
            if (minute >= 50 && hour.equals(time_num)) continue;
            ret.add(time_num);
        }

        Collections.sort(ret);

        return ret;
    }

    public void resRoom(String user_id, Integer room_num, String date, String time)
    {
        roomResEntity roomRes = new roomResEntity();
        roomRes.setUserId(user_id);
        roomRes.setRoomNum(room_num);
        roomRes.setDate(date);
        roomRes.setResStart(time);
        RoomResRepository.save(roomRes);
    }

    @Transactional
    public void resChange(Integer res_id, String changeTime)
    {
        Optional<roomResEntity> roomRes = RoomResRepository.findById(res_id);
        if (roomRes.isPresent()) {
            roomRes.get().setResStart(changeTime);
            RoomResRepository.save(roomRes.get());
        }
    }

    @Transactional
    public void resDelete(Integer res_id)
    {
        Optional<roomResEntity> roomRes = RoomResRepository.findById(res_id);
        roomRes.ifPresent(roomResEntity -> RoomResRepository.delete(roomResEntity));
    }
}
