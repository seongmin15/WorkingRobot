package com.wor.server.controller;

import com.wor.server.domain.dto.*;
import com.wor.server.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class userController {
    @Autowired
    private userService UserService;

    @GetMapping("/signup/company")
    ResponseEntity<?> getComNames() {
        List<String> comNames = UserService.getComNames();

        comName response = new comName();
        response.setMessage("Success");
        response.setCompany(comNames);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/signup/department/{com_name}")
    ResponseEntity<?> getDepNames(@PathVariable("com_name") String com_name) {
        List<String> depNames = UserService.getDepNames(com_name);

        depName response = new depName();
        response.setMessage("Success");
        response.setDepartment(depNames);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/signup/seat/{Info}")
    ResponseEntity<?> getSeatInfo(@PathVariable("Info") String info) {
        String[] infoList = info.split("_");
        String com_name = infoList[0];
        String dep_name = infoList[1];

        List<Integer> seatFillList = UserService.getSeatInfo(com_name, dep_name);

        seatFill response = new seatFill();
        response.setMessage("Success");
        response.setAlready_seat(seatFillList);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/signup/idcheck")
    ResponseEntity<?> checkUserId(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();

        String result = UserService.checkUserId(user_id);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", result);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/signup/serialcheck")
    ResponseEntity<?> checkSerial(@RequestBody HashMap<String, Object> param) {
        String com_name = param.get("company").toString();
        String dep_name = param.get("department").toString();
        String robot_serial = param.get("robot_serial").toString();

        String result = UserService.checkSerial(com_name, dep_name, robot_serial);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", result);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/signup")
    ResponseEntity<?> signUp(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        String user_name = param.get("user_name").toString();
        String user_pw = param.get("password").toString();
        String robot_serial = param.get("robot_serial").toString();
        String com_name = param.get("company").toString();
        String dep_name = param.get("department").toString();
        Integer seat_num = Integer.parseInt(param.get("seat_num").toString());

        HashMap<String, Object> response = new HashMap<>();

        if (UserService.signUp(user_id, user_pw, user_name, com_name, dep_name, seat_num, robot_serial))
            response.put("message", "success");
        else response.put("message", "failure");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/signin")
    ResponseEntity<?> signIn(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        String user_pw = param.get("password").toString();

        userInfo user = UserService.signIn(user_id, user_pw);

        userResponseDto response = new userResponseDto();

        if (user == null) response.setMessage("failure");
        else response.setMessage("success");
        response.setUserInfo(user);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PutMapping("/pwchange")
    ResponseEntity<?> pwChange(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        String old_pw = param.get("old_password").toString();
        String new_pw = param.get("new_password").toString();

        HashMap<String, Object> response = new HashMap<>();

        if (UserService.pwChange(user_id, old_pw, new_pw)) response.put("message", "success");
        else response.put("message", "failure");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PutMapping("/namechange")
    ResponseEntity<?> nameChange(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        String user_name = param.get("user_name").toString();

        UserService.nameChange(user_id, user_name);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "success");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @DeleteMapping("/signout")
    ResponseEntity<?> signOut(@RequestBody HashMap<String, Object> param) {
        String user_id = param.get("user_id").toString();
        String user_pw = param.get("password").toString();

        HashMap<String, Object> response = new HashMap<>();

        if (UserService.deleteUser(user_id, user_pw)) response.put("message", "success");
        else response.put("message", "failure");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }
}
