package com.sky.service.impl;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间区域内的营业额数据
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> listDate = new ArrayList<>();

        while (!beginDate.isAfter(endDate)) {
            listDate.add(beginDate);
            beginDate = beginDate.plusDays(1);
        }

        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();

        for(LocalDate date:listDate){
            //查询date日期对应的营业额数据，订单已完成的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // select sum(amount) from orders where order_time? and status = 5
            Map map = new HashMap();
            map.put("beginDate", beginTime);
            map.put("endDate", endTime);
            map.put("status", Orders.COMPLETED);
            Double tuurnover = orderMapper.sumByMap(map);
            tuurnover = tuurnover==null?0.0:tuurnover;
            turnoverList.add(tuurnover);
        }

        //封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(listDate,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}
