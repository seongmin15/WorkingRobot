package com.wor.server.service;

import com.wor.server.domain.dto.userInfo;
import com.wor.server.domain.entity.*;
import com.wor.server.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class userService {
    @Autowired
    private comRepository ComRepository;
    @Autowired
    private depRepository DepRepository;
    @Autowired
    private areaRepository AreaRepository;
    @Autowired
    private seatRepository SeatRepository;
    @Autowired
    private userRepository UserRepository;

    private String getSerial(Integer area_num) {
        Optional<areaEntity> area = AreaRepository.findById(area_num);
        return area.get().getRobotSerial();
    }

    private Integer getComNum(String com_name) {
        Optional<comEntity> com = ComRepository.findByComName(com_name);
        return com.get().getComNum();
    }

    private List<Integer> getDepNumAndAreaNum(String com_name, String dep_name) {
        List<Integer> ret = new ArrayList<>();

        Optional<depEntity> dep = DepRepository.findByComNameAndDepName(com_name, dep_name);

        ret.add(dep.get().getDepNum());
        ret.add(dep.get().getAreaNum());

        return ret;
    }

    public List<String> getComNames()
    {
        List<String> ret = new ArrayList<>();

        List<comEntity> comList = ComRepository.findAll();

        for (comEntity com : comList) {
            ret.add(com.getComName());
        }

        return ret;
    }

    public List<String> getDepNames(String com_name)
    {
        List<String> ret = new ArrayList<>();

        List<depEntity> depList = DepRepository.findAllByComName(com_name);

        for (depEntity dep : depList) {
            ret.add(dep.getDepName());
        }

        return ret;
    }

    public List<Integer> getSeatInfo(String com_name, String dep_name)
    {
        List<Integer> ret = new ArrayList<>();
        Optional<depEntity> dep = DepRepository.findByComNameAndDepName(com_name, dep_name);

        Integer area_num = dep.get().getAreaNum();
        List<Optional<seatEntity>> seatList = SeatRepository.findAllByAreaNum(area_num);

        for (Optional<seatEntity> seat : seatList) {
            if (seat.get().getFill()) ret.add(seat.get().getSeatNum());
        }

        return ret;
    }

    public String checkUserId(String user_id)
    {
        Optional<userEntity> target = UserRepository.findById(user_id);

        if (target.isPresent()) return "true";
        else return "false";
    }

    public String checkSerial(String com_name, String dep_name, String robot_serial)
    {
        Optional<depEntity> dep = DepRepository.findByComNameAndDepName(com_name, dep_name);

        Integer area_num = dep.get().getAreaNum();
        try {
            String target_serial = getSerial(area_num);
            if (target_serial.equals(robot_serial)) return "success";
        } catch (Exception e) {
            return "failure";
        }

        return "failure";
    }

    public Boolean signUp(String user_id, String user_pw, String user_name, String com_name, String dep_name, Integer seat_num, String robot_serial)
    {
        Optional<userEntity> userCheck = UserRepository.findByUserName(user_name);
        if (userCheck.isPresent()) return false;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String securePw = passwordEncoder.encode(user_pw);

        Integer com_num = getComNum(com_name);
        List<Integer> depAndArea = getDepNumAndAreaNum(com_name, dep_name);
        Integer dep_num = depAndArea.get(0);
        Integer area_num = depAndArea.get(1);

        userEntity user = new userEntity();
        user.setUserId(user_id);
        user.setUserPw(securePw);
        user.setUserName(user_name);
        user.setComNum(com_num);
        user.setDepNum(dep_num);
        user.setAreaNum(area_num);
        user.setSeatNum(seat_num);
        user.setRobotSerial(robot_serial);
        UserRepository.save(user);

        seatId id = new seatId(seat_num, area_num);
        Optional<seatEntity> seat = SeatRepository.findById(id);
        seat.get().setFill(true);
        SeatRepository.save(seat.get());
        return true;
    }

    public userInfo signIn(String user_id, String user_pw)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<userEntity> User = UserRepository.findByUserId(user_id);
        if (User.isPresent()) {
            userEntity user = User.get();
            if (passwordEncoder.matches(user_pw, user.getUserPw())) {
                log.info("login");
                Optional<comEntity> com = ComRepository.findById(user.getComNum());
                String comName = com.get().getComName();
                Optional<depEntity> dep = DepRepository.findById(user.getDepNum());
                String depName = dep.get().getDepName();
                userInfo ret =  new userInfo();
                ret.setUserId(user.getUserId());
                ret.setUserName(user.getUserName());
                ret.setRobotSerial(user.getRobotSerial());
                ret.setComName(comName);
                ret.setComNum(user.getComNum());
                ret.setDepName(depName);
                ret.setDepNum(user.getDepNum());
                ret.setAreaNum(user.getAreaNum());
                ret.setSeatNum(user.getSeatNum());
                return ret;
            }
        }
        return null;
    }

    public Boolean pwChange(String user_id, String old_pw, String new_pw) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<userEntity> User = UserRepository.findByUserId(user_id);
        if (User.isPresent()) {
            userEntity user = User.get();
            if (passwordEncoder.matches(old_pw, user.getUserPw())) {
                user.setUserPw(passwordEncoder.encode(new_pw));
                UserRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public void nameChange(String user_id, String user_name)
    {
        Optional<userEntity> user = UserRepository.findByUserId(user_id);
        user.get().setUserName(user_name);
        UserRepository.save(user.get());
    }

    @Transactional
    public Boolean deleteUser(String user_id, String user_pw) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<userEntity> User = UserRepository.findByUserId(user_id);
        if (User.isPresent()) {
            userEntity user = User.get();
            if (passwordEncoder.matches(user_pw, user.getUserPw())) {
                UserRepository.delete(user);
                return true;
            }
        }
        return false;
    }
}
