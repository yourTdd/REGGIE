package com.lt.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.*;
import com.lt.reggie.service.SetmealDishService;
import com.lt.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 分页查询和条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    /**
     * 更改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable String status, @RequestParam List ids){
        boolean b = setmealService.updateStatus(status, ids);
        return b?R.success("更新状态成功！"):R.error("更新失败！");
    }

    /**
     * 删除套餐的同时删除对应的关联信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List ids){
        boolean flag = setmealService.deleteWithDish(ids);
        return flag?R.success("删除成功！"):R.error("删除失败");
    }

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        boolean flag = setmealService.saveWithDish(setmealDto);
        return flag?R.success("新增成功！"):R.error("新增失败！");
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDishById(id);
        return R.success(setmealDto);
    }

    /**
     * 保存修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        boolean flag = setmealService.updateWithDish(setmealDto);
        return flag?R.success("修改成功！"):R.error("修改失败");
    }


    /**
     * 小程序套餐查询
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        List<Setmeal> list = setmealService.getList(setmeal);
        return list!=null?R.success(list):R.error("查询失败！");
    }

    /**
     * 获取套餐的全部菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDto>> getDishBySetmeal(@PathVariable String id){
        List<SetmealDto> setmealDtoList = setmealDishService.getBySetmealId(id);
        return setmealDtoList != null?R.success(setmealDtoList):R.error("查询失败");
    }

}
