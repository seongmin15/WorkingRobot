package com.wor.server.controller;

import com.wor.server.domain.dto.depName;
import com.wor.server.domain.dto.elecResponseDto;
import com.wor.server.domain.dto.voteResponseDto;
import com.wor.server.domain.entity.voteEntity;
import com.wor.server.domain.repository.voteInfo;
import com.wor.server.service.voteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vote")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class voteController {
    @Autowired
    private voteService VoteService;

    @GetMapping("/elecInfo/{area_num}")
    ResponseEntity<?> getElecNames(@PathVariable("area_num") Integer area_num) {
        List<Map<String, Object>> elec = VoteService.getElecInfo(area_num);

        elecResponseDto response = new elecResponseDto();
        response.setMessage("Success");
        response.setElec_list(elec);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/voteInfo/{area_num}")
    ResponseEntity<?> getDepNames(@PathVariable("area_num") Integer area_num) {
        List<voteInfo> vote = VoteService.getVoteInfo(area_num);

        voteResponseDto response = new voteResponseDto();
        response.setMessage("Success");
        response.setVote_list(vote);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/create")
    ResponseEntity<?> checkUserId(@RequestBody HashMap<String, Object> param) throws InterruptedException {
        String title = param.get("title").toString();
        Integer area_num = Integer.parseInt(param.get("area_num").toString());
        Integer elec_num = Integer.parseInt(param.get("elec_num").toString());
        String user_id = param.get("user_id").toString();

        VoteService.createVote(elec_num, area_num, title, user_id);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/dovote")
    ResponseEntity<?> vote(@RequestBody HashMap<String, Object> param) {
        Integer elec_num = Integer.parseInt(param.get("elec_num").toString());
        Boolean agree = Boolean.parseBoolean(param.get("agree").toString());

        VoteService.vote(elec_num, agree);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
