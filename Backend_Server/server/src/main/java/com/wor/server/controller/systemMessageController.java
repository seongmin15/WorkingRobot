package com.wor.server.controller;

import com.wor.server.domain.dto.systemMessageResponseDto;
import com.wor.server.service.alarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/systemmassage")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class systemMessageController {
    @Autowired
    private alarmService AlarmService;

    @GetMapping("/{user_id}")
    ResponseEntity<?> getRoomInfo(@PathVariable("user_id") String user_id) {
        List<Map<String, Object>> messageList = AlarmService.getMessageList(user_id);

        systemMessageResponseDto response = new systemMessageResponseDto();
        response.setMessage("Success");
        response.setMessage_list(messageList);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
