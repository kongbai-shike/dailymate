package com.xsy.dailymate.service;

import com.xsy.dailymate.dto.BillStatistics;
import com.xsy.dailymate.entity.Bill;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BillService {
    Bill addBill(Bill bill);
    Bill updateBill(Bill bill);
    void deleteBill(Long id);
    Bill findById(Long id);
    List<Bill> getBillListByUserAndDate(Long userId, Date date);
    List<Bill> getBillListByUser(Long userId);

    // 新增：日期范围筛选
    List<Bill> getBillListByUserAndDateRange(Long userId, Date start, Date end);

    // 新增：类型筛选（1-收入，2-支出，0全部）
    List<Bill> getBillListByUserAndType(Long userId, Integer type, Date start, Date end);

    // 新增：分类筛选
    List<Bill> getBillListByUserAndCategory(Long userId, String category, Date start, Date end);

    // 新增：金额区间筛选
    List<Bill> getBillListByUserAndAmountBetween(Long userId, Double minAmount, Double maxAmount, Date start, Date end);

    // 新增：模糊查询
    List<Bill> searchBill(Long userId, String keyword);

    // 新增：月收支统计
    Map<String, Double> statMonth(Long userId, Integer year, Integer month);

    // 新增：分类统计（某段时间内）
    Map<String, Double> statCategory(Long userId, Integer type, Date start, Date end);

    // 新增：趋势分析
    List<Map<String, Object>> statTrend(Long userId, Integer days);

    // 新增：批量软删除
    void batchDelete(List<Long> ids);

    // 新增：彻底物理删除
    void hardDelete(Long id);

    // 新增：批量彻底物理删除
    void batchHardDelete(List<Long> ids);

    // 新增：账单恢复
    void restore(Long id);

    // 新增：账单统计
    BillStatistics getStatistics(Long userId, Integer year, Integer month);
}
