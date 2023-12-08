package com.wor.server.controller;

import com.wor.server.domain.entity.robotEntity;
import com.wor.server.domain.repository.resRepository;
import com.wor.server.domain.repository.robotRepository;
import com.wor.server.service.reservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@EnableScheduling
public class socket_serverController {
    private static final Logger LOGGER = LoggerFactory.getLogger( socket_serverController.class );

    private final SimpMessageSendingOperations simpleMessageSendingOperations;
    @Autowired
    robotRepository RobotRepository;
    @Autowired
    resRepository ResRepository;
    @Autowired
    reservationService ReservationService;

    // 새로운 사용자가 웹 소켓을 연결할 때 실행됨
    // @EventListener은 한개의 매개변수만 가질 수 있다.
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        LOGGER.info("Received a new web socket connection");
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccesor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccesor.getSessionId();

        LOGGER.info("sessionId Disconnected : " + sessionId);
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void sendOrder()
    {
        int order_size = ReservationService.orderQueue.size();
//        log.info(ReservationService.orderQueue.toString());
//        log.info("size:" + order_size);

        for (Map<String, Object> order : ReservationService.orderQueue) {
            if (Objects.equals(order.get("type").toString(), "trash")) {
                sendTrashMessage(order);
            } else if (Objects.equals(order.get("type").toString(), "room")) {
                sendRoomMessage(order);
            } else if (Objects.equals(order.get("type").toString(), "elec")) {
                sendElecMessage(order);
            }

            Optional<robotEntity> robot = RobotRepository.findById(order.get("robot_serial").toString());
            robot.get().setProcess(1);
            RobotRepository.save(robot.get());
        }

        if (order_size > 0) {
            ResRepository.deleteAllByProcess(1);
            ReservationService.orderQueue.clear();
        }
    }

    // /pub/trash 로 메시지를 발행한다.
    @MessageMapping("/trash")
    @SendTo("/sub/trash")
    public void sendTrashMessage(Map<String, Object> params) {

        // /sub/cache 에 구독중인 client에 메세지를 보낸다.
        LOGGER.info("params : " + params);
        simpleMessageSendingOperations.convertAndSend("/sub/trash/" + params.get("robot_serial"), params);
    }

    @MessageMapping("/room")
    @SendTo("/sub/room")
    public void sendRoomMessage(Map<String, Object> params) {

        // /sub/cache 에 구독중인 client에 메세지를 보낸다.
        LOGGER.info("params : " + params);
        simpleMessageSendingOperations.convertAndSend("/sub/room/" + params.get("robot_serial"), params);
    }

    @MessageMapping("/elec")
    @SendTo("/sub/elec")
    public void sendElecMessage(Map<String, Object> params) {

        // /sub/cache 에 구독중인 client에 메세지를 보낸다.
        LOGGER.info("params : " + params);
        simpleMessageSendingOperations.convertAndSend("/sub/elec/" + params.get("robot_serial"), params);
    }

    @MessageMapping("/location")
    @SendTo("/sub/location")
    public void sendLocationMessage(Map<String, Object> params) {

        // /sub/cache 에 구독중인 client에 메세지를 보낸다.
        simpleMessageSendingOperations.convertAndSend("/sub/location/" + params.get("robot_serial"), params);
    }

    @MessageMapping("/finish")
    public void getFinishMessage(Map<String, Object> params) {

        LOGGER.info("finish params : " + params);

        String serial = params.get("robot_serial").toString();
        ReservationService.processFinish(serial);
    }
}
