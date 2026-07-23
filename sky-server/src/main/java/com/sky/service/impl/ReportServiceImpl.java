package com.sky.service.impl;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

    @Autowired
    private UserMapper userMapper;



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

    /**
     * 统计用户
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getuerStatistics(LocalDate begin, LocalDate end) {

        //封装好每一个List
        List<LocalDate> listDate = new ArrayList<>();
        while (!begin.isAfter(end)) {
            listDate.add(begin);
            begin = begin.plusDays(1);
        }
        //存放两种用户的数量
        List<Integer> newUserList = new ArrayList<>();

        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date:listDate){
            //查询date日期对应的营业额数据，订单已完成的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            totalUser=totalUser==null?0:totalUser;
            newUser=newUser==null?0:newUser;
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }


        //封装返回结果
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(listDate,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {

        //封装好每一个List
        List<LocalDate> listDate = new ArrayList<>();
        while (!begin.isAfter(end)) {
            listDate.add(begin);
            begin = begin.plusDays(1);
        }

        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> vaildOrderList = new ArrayList<>();
        //遍历日期集合，查询每天的有效订单数以及订单总数
        for (LocalDate date:listDate){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            map.put("begin", beginTime);
            //查询每天的总订单数
            Integer totalOrder = orderMapper.countByMap(map);
            totalOrder=totalOrder==null?0:totalOrder;
            map.put("status", Orders.COMPLETED);
            //查询每天的有效订单数
            Integer vaildOrder = orderMapper.countByMap(map);
            vaildOrder=vaildOrder==null?0:vaildOrder;
            totalOrderList.add(totalOrder);
            vaildOrderList.add(vaildOrder);
        }
        //计算订单总量
        Integer totalOrderCount = totalOrderList.stream().reduce(0, Integer::sum);
        Integer vaildTotalOrderCount = vaildOrderList.stream().reduce(0, Integer::sum);
        Double orderCompletionRate  = 0.0;
            if (totalOrderCount!=0){
                orderCompletionRate = (double) (vaildTotalOrderCount/totalOrderCount);
            }
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(listDate,","))
                .orderCountList(StringUtils.join(totalOrderList,","))
                .validOrderCountList(StringUtils.join(vaildOrderList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(vaildTotalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}
