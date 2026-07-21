package com.sky.controller.admin;


import com.sky.constant.RedisKeyNameConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result saveDish(@RequestBody DishDTO dishDTO){

        log.info("新增菜品：{}",dishDTO);
        dishService.saveDishWithFlavor(dishDTO);

        //清理redis的缓存数据
        cleanCache(RedisKeyNameConstant.DISH_KEY_NAME + dishDTO.getCategoryId());

        return  Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO){

        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("菜品批量删除，{}",ids);
        dishService.deleteBatch(ids);

        cleanCache(RedisKeyNameConstant.DISH_KEYS_NAME);

        return Result.success();
    }

    /**
     * 根据具体的ID来查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result<DishVO> getByID(@PathVariable Long id ){
        log.info("根据ID来查询菜品数据{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品的数据
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品数据")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        cleanCache(RedisKeyNameConstant.DISH_KEYS_NAME);

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品的停售和起售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result updateStatus(@PathVariable Integer status,Long id) {
        log.info("菜品ID为{},要{}",id,status);
        dishService.updateStatus(status,id);

        cleanCache(RedisKeyNameConstant.DISH_KEYS_NAME);
        return Result.success();
    }

    /**
     * 统一清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
