package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.Dish;
import com.lt.reggie.entity.DishDto;

import java.util.List;


public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish，dish_flavor
    void saveWithFlavor(DishDto dishDto);

    // 根据id来查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    // 根据id查询菜品
    List<DishDto> get(Dish dish);
}
