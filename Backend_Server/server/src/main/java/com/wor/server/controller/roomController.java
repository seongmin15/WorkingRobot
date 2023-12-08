package com.wor.server.controller;

import com.wor.server.domain.dto.roomInfo;
import com.wor.server.domain.dto.roomResponseDto;
import com.wor.server.domain.dto.roomTimeInfo;
import com.wor.server.domain.dto.trashResponseDto;
import com.wor.server.domain.entity.roomResEntity;
import com.wor.server.service.roomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/meeting")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class roomController {
    @Autowired
    private roomService RoomService;

    @GetMapping("/roomInfo/{com_num}")
    ResponseEntity<?> getRoomInfo(@PathVariable("com_num") Integer com_num) {
        List<Map<String, Integer>> roomNames = RoomService.getRoomNames(com_num);

        roomInfo response = new roomInfo();
        response.setMessage("Success");
        response.setRoomNames(roomNames);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{userId}")
    ResponseEntity<?> getRoomRes(@PathVariable("userId") String userId) {
        log.info("user_id:" + userId);

        List<Optional<roomResEntity>> roomRes = RoomService.getUserRoomRes(userId);

        roomResponseDto response = new roomResponseDto();
        response.setMessage("Success");
        response.setRoomRes(roomRes);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/time/{resInfo}")
    ResponseEntity<?> getRoomResTime(@PathVariable("resInfo") String resInfo) {
        String[] resInfo_arr = resInfo.split("_");
        String date = resInfo_arr[0];
        Integer room_name = Integer.parseInt(resInfo_arr[1]);

        List<Integer> times = RoomService.getRoomResTime(date, room_name);

        roomTimeInfo response = new roomTimeInfo();
        response.setMessage("Success");
        response.setTimes(times);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/res")
    ResponseEntity<?> roomRes(@RequestBody HashMap<String, Object> param) {
        log.info(param.toString());
        String userId = param.get("user_id").toString();
        Integer roomNum = Integer.parseInt(param.get("room_num").toString());
        String[] dateFormat = param.get("date").toString().split(" ");
        String date = dateFormat[0];
        String time = dateFormat[1];

        RoomService.resRoom(userId, roomNum, date, time);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PutMapping("/res/change")
    ResponseEntity<?> roomResChange(@RequestBody HashMap<String, Object> param) {
        Integer res_id = Integer.parseInt(param.get("resNum").toString());
        String changeTime = param.get("time").toString();

        RoomService.resChange(res_id, changeTime);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @DeleteMapping("/res/delete/{resNum}")
    ResponseEntity<?> roomResDelete(@PathVariable("resNum") Integer resNum) {
        RoomService.resDelete(resNum);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
