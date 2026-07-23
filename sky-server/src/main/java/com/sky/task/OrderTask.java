package com.sky.task;


import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理超时订单
     */
//    @Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "2/5 * * * * ?")
    public void processTimeOut(){
        log.info("定时处理超时订单{},", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now);
        if (ordersList!=null&&ordersList.size()>0){
            ordersList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason(MessageConstant.ORDER_TIME_OUT_CANCELED);
                orderMapper.update(order);
            });
        }

    }

    /**
     * 处理一直处于派送中的订单，自动完成
     */
    @Scheduled(cron = "0/5 * * * * ?")
//    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理派送中的订单{},", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, now);
        if (ordersList!=null&&ordersList.size()>0){
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
