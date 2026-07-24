package com.sky.service;

import com.sky.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {


    /**
     * 统计指定时间内的营业额数据
     * @param beginDate
     * @param endDate
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate);

    /**
     * 统计每日用户以及总用户
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getuerStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排名前10
     *
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end);

    /**
     * 导出运营数据报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
