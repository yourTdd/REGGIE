package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.Dish;
import com.lt.reggie.entity.SetmealDish;
import com.lt.reggie.entity.SetmealDto;

import java.util.List;

public interface SetmealDishService extends IService<SetmealDish> {
    // 获取套餐的全部菜品
    List<SetmealDto> getBySetmealId(String id);
}
