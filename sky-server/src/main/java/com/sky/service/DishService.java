package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品以及对应的口味
     * @param dishDTO
     */

    public void  saveDishWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据ID查询菜品和口味数据
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据菜品ID来修改菜品和数据口味信息
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);


    /**
     * 根据分类ID来查询菜品——管理端
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);
}
