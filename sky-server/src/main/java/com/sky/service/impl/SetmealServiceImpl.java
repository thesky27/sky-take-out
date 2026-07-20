package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetMealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //向套餐中插入数据
        setmealMapper.insert(setmeal);

        //获取套餐的ID
        Long setmealId = setmeal.getId();
        log.info("目前套餐的ID是{}", setmealId);
        //获取套餐下的所有菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //为套餐里面的所有菜品设置套餐ID
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});

        //批量保存所需要的数据
        setmealDishMapper.insertBatch(setmealDishes);

    }
}
