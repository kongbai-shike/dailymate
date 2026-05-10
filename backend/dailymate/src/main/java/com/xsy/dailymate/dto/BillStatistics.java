package com.xsy.dailymate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 账单统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillStatistics {

    /**
     * 总收入
     */
    private double totalIncome;

    /**
     * 总支出
     */
    private double totalExpense;

    /**
     * 结余
     */
    private double balance;

    /**
     * 分类统计（分类名 -> 金额）
     */
    private Map<String, Double> categoryStats;

    /**
     * 月度趋势（月份 -> 收支数据）
     */
    private List<MonthlyTrend> monthlyTrend;

    /**
     * 月度趋势数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String month;
        private double income;
        private double expense;
    }
}
