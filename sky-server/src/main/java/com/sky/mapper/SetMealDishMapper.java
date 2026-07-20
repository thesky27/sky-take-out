package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<Long> getSetMealIdByDishIds(List<Long> dishIds);


    /**
     * 批量保存套餐与菜品的关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);
}
