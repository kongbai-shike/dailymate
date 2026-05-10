package com.xsy.dailymate.controller;

import com.xsy.dailymate.common.Result;
import com.xsy.dailymate.dto.BillStatistics;
import com.xsy.dailymate.dto.request.BillRequest;
import com.xsy.dailymate.entity.Bill;
import com.xsy.dailymate.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账单控制器
 */
@RestController
@RequestMapping("/api/bill")
@Validated
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * 获取账单列表
     */
    @GetMapping("/list")
    public Result<List<Bill>> getBillList(@RequestParam Long userId,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Bill> bills;
        if (date != null) {
            bills = billService.getBillListByUserAndDate(userId, date);
        } else {
            bills = billService.getBillListByUser(userId);
        }
        return Result.success(bills);
    }

    /**
     * 获取账单详情
     */
    @GetMapping("/{id}")
    public Result<Bill> findById(@PathVariable Long id) {
        Bill bill = billService.findById(id);
        if (bill == null) {
            return Result.error(404, "账单不存在");
        }
        return Result.success(bill);
    }

    /**
     * 添加账单
     */
    @PostMapping("/add")
    public Result<Bill> addBill(@Valid @RequestBody BillRequest request) {
        Bill bill = new Bill();
        bill.setUserId(request.getUserId());
        bill.setType(request.getType());
        bill.setCategory(request.getCategory());
        bill.setAmount(request.getAmount());
        bill.setRemark(request.getRemark());
        bill.setDate(request.getDate());

        Bill saved = billService.addBill(bill);
        return Result.success("添加成功", saved);
    }

    /**
     * 更新账单
     */
    @PutMapping("/update")
    public Result<Bill> updateBill(@Valid @RequestBody BillRequest request) {
        Bill existing = billService.findById(request.getId());
        if (existing == null) {
            return Result.error(404, "账单不存在");
        }

        existing.setType(request.getType());
        existing.setCategory(request.getCategory());
        existing.setAmount(request.getAmount());
        existing.setRemark(request.getRemark());
        existing.setDate(request.getDate());

        Bill updated = billService.updateBill(existing);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除账单（软删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return Result.successMessage("删除成功");
    }

    /**
     * 按日期范围过滤
     */
    @GetMapping("/range")
    public Result<List<Bill>> getBillsInRange(@RequestParam Long userId,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Bill> bills = billService.getBillListByUserAndDateRange(userId, start, end);
        return Result.success(bills);
    }

    /**
     * 按类型过滤
     */
    @GetMapping("/by-type")
    public Result<List<Bill>> getBillsByType(@RequestParam Long userId,
                                              @RequestParam Integer type,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Bill> bills = billService.getBillListByUserAndType(userId, type, start, end);
        return Result.success(bills);
    }

    /**
     * 按分类过滤
     */
    @GetMapping("/by-category")
    public Result<List<Bill>> getBillsByCategory(@RequestParam Long userId,
                                                  @RequestParam String category,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Bill> bills = billService.getBillListByUserAndCategory(userId, category, start, end);
        return Result.success(bills);
    }

    /**
     * 按金额区间过滤
     */
    @GetMapping("/by-amount-range")
    public Result<List<Bill>> getBillsByAmountRange(@RequestParam Long userId,
                                                     @RequestParam Double min,
                                                     @RequestParam Double max,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        List<Bill> bills = billService.getBillListByUserAndAmountBetween(userId, min, max, start, end);
        return Result.success(bills);
    }

    /**
     * 备注关键字模糊查询
     */
    @GetMapping("/search")
    public Result<List<Bill>> searchBill(@RequestParam Long userId,
                                          @RequestParam String keyword) {
        List<Bill> bills = billService.searchBill(userId, keyword);
        return Result.success(bills);
    }

    /**
     * 月度收支统计
     */
    @GetMapping("/stat/month")
    public Result<Map<String, Double>> statMonth(@RequestParam Long userId,
                                                  @RequestParam Integer year,
                                                  @RequestParam Integer month) {
        Map<String, Double> stats = billService.statMonth(userId, year, month);
        return Result.success(stats);
    }

    /**
     * 分类统计
     */
    @GetMapping("/stat/category")
    public Result<Map<String, Double>> statCategory(@RequestParam Long userId,
                                                     @RequestParam Integer type,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Map<String, Double> stats = billService.statCategory(userId, type, start, end);
        return Result.success(stats);
    }

    /**
     * 近 N 天收支趋势
     */
    @GetMapping("/stat/trend")
    public Result<List<Map<String, Object>>> statTrend(@RequestParam Long userId,
                                                        @RequestParam Integer days) {
        List<Map<String, Object>> trend = billService.statTrend(userId, days);
        return Result.success(trend);
    }

    /**
     * 批量软删除
     */
    @PutMapping("/batch/delete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");

        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的项目");
        }

        billService.batchDelete(ids.stream().map(Long::valueOf).toList());
        return Result.successMessage("批量删除成功");
    }

    /**
     * 批量彻底物理删除
     */
    @PutMapping("/batch/hard-delete")
    public Result<Void> batchHardDelete(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");

        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的项目");
        }

        billService.batchHardDelete(ids.stream().map(Long::valueOf).toList());
        return Result.successMessage("批量彻底删除成功");
    }

    /**
     * 恢复软删除
     */
    @PutMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        billService.restore(id);
        return Result.successMessage("恢复成功");
    }

    /**
     * 彻底物理删除
     */
    @DeleteMapping("/hard-delete/{id}")
    public Result<Void> hardDelete(@PathVariable Long id) {
        billService.hardDelete(id);
        return Result.successMessage("彻底删除成功");
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<BillStatistics> getStatistics(
        @RequestParam Long userId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month
    ) {
        BillStatistics statistics = billService.getStatistics(userId, year, month);
        return Result.success(statistics);
    }
}
