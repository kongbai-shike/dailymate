package com.xsy.dailymate.repository;

import com.xsy.dailymate.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserIdAndIsDelete(Long userId, Integer isDelete);
    List<Bill> findByUserIdAndDateAndIsDelete(Long userId, Date date, Integer isDelete);

    // 日期范围
    List<Bill> findByUserIdAndIsDeleteAndDateBetween(Long userId, Integer isDelete, Date start, Date end);

    // 类型
    List<Bill> findByUserIdAndTypeAndIsDeleteAndDateBetween(Long userId, Integer type, Integer isDelete, Date start, Date end);

    // 分类
    List<Bill> findByUserIdAndCategoryAndIsDeleteAndDateBetween(Long userId, String category, Integer isDelete, Date start, Date end);

    // 金额区间
    List<Bill> findByUserIdAndIsDeleteAndAmountBetweenAndDateBetween(Long userId, Integer isDelete, Double min, Double max, Date start, Date end);

    // 模糊查询
    List<Bill> findByUserIdAndIsDeleteAndRemarkContaining(Long userId, Integer isDelete, String keyword);
}
