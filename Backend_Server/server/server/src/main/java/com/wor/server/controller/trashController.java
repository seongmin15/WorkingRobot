package com.wor.server.controller;

import com.wor.server.domain.dto.trashResponseDto;
import com.wor.server.service.trashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/trash")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class trashController {
    @Autowired
    trashService TrashService;
    @PostMapping("/res")
    ResponseEntity<?> reservation(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        Integer seat_num = Integer.parseInt(param.get("seat_num").toString());
        Integer area_num = Integer.parseInt(param.get("area_num").toString());

        log.info("seat:" + seat_num + ", area:" + area_num);

        Integer remain = TrashService.resTrash(user_id, seat_num, area_num);
        trashResponseDto response = new trashResponseDto();
        response.setMessage("Success");
        response.setQueue_num(remain);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{userId}")
    ResponseEntity<?> time(@PathVariable("userId") String userId) {
        log.info("user_id:" + userId);

        Integer queue_num = TrashService.getUserQueueNum(userId);
        trashResponseDto response = new trashResponseDto();
        response.setMessage("Success");
        response.setQueue_num(queue_num);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
