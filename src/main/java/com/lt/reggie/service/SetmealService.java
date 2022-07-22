package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.Setmeal;
import com.lt.reggie.entity.SetmealDto;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    // 分页查询和条件查询
    Page<SetmealDto> page(int page,int pageSize,String name);

    // 更新状态
    boolean updateStatus(String status, List ids);

    // 新增套餐
    boolean saveWithDish(SetmealDto setmealDto);

    // 删除套餐
    boolean deleteWithDish(List<Long> ids);

    // 查询详情
    SetmealDto getWithDishById(Long id);

    // 修改并保存到两张表
    boolean updateWithDish(SetmealDto setmealDto);

    // 小程序查询
    List<Setmeal> getList(Setmeal setmeal);
}
