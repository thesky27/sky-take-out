package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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

    /**
     * 套餐进行分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new  PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 实现套餐的批量删除
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        log.info("批量删除套餐的ID为{}", ids);
        ids.forEach(setmealId -> {
            Setmeal setmeal = setmealMapper.getById(setmealId);
            if (setmeal.getStatus()== StatusConstant.ENABLE){
                //该套餐如果正在售卖中
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(setmealId -> {
            //根据ID来删除套餐数据
            setmealMapper.deleteById(setmealId);
            //删除套管菜品关系表中的数据
            setmealDishMapper.deleBySetmealId(setmealId);
        });
    }
}
