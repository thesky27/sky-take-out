package com.sky.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */
@Slf4j
@Component
public class MyTask {

//    @Scheduled(cron = "0/5 * * * * ?")
    public void excuteTask (){
        log.info("定时任务开始执行，{}",new Date());
    }

}
