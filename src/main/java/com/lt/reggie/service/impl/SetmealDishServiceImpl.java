package com.lt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.reggie.entity.Dish;
import com.lt.reggie.entity.Setmeal;
import com.lt.reggie.entity.SetmealDish;
import com.lt.reggie.entity.SetmealDto;
import com.lt.reggie.mapper.SetmealDishMapper;
import com.lt.reggie.service.DishService;
import com.lt.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    private DishService dishService;

    /**
     * 获取套餐的全部菜品
     * @param id
     * @return
     */
    @Override
    public List<SetmealDto> getBySetmealId(String id) {
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = this.list(qw);

        List<SetmealDto> setmealDtoList = list.stream().map((i) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(i, setmealDto);

            Long dishId = i.getDishId();
            String image = dishService.getById(dishId).getImage();
            if (image != null) {
                setmealDto.setImage(image);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        return setmealDtoList;
    }
}
