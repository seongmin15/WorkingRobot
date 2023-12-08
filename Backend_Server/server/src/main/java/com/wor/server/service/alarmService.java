package com.wor.server.service;

import com.wor.server.domain.entity.alarmEntity;
import com.wor.server.domain.repository.alarmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class alarmService {
    @Autowired
    private alarmRepository AlarmRepository;

    public void save(String user_id, String type, Boolean start_end, String content)
    {
        LocalTime now = LocalTime.now(Clock.system(ZoneId.of("Asia/Seoul")));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = now.format(dateFormat);

        alarmEntity alarm = new alarmEntity();
        alarm.setUserId(user_id);
        alarm.setType(type);
        alarm.setStartEnd(start_end);
        alarm.setDate(date);
        alarm.setContent(content);
        AlarmRepository.save(alarm);
    }

    public List<Map<String, Object>> getMessageList(String user_id)
    {
        List<Map<String, Object>> ret = new ArrayList<>();
        List<Optional<alarmEntity>> alarmList = AlarmRepository.findAllByUserId(user_id);
        Map<String, Object> tmp = new HashMap<>();

        for (Optional<alarmEntity> alarm : alarmList) {
            tmp.put("type", alarm.get().getType());
            tmp.put("startEnd", alarm.get().getStartEnd());
            tmp.put("date", alarm.get().getDate());
            tmp.put("content", alarm.get().getContent());
            ret.add(tmp);
            tmp.clear();
        }

        return ret;
    }
}
