package com.lt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.Category;
import com.lt.reggie.entity.Setmeal;
import com.lt.reggie.entity.SetmealDish;
import com.lt.reggie.entity.SetmealDto;
import com.lt.reggie.mapper.SetmealMapper;
import com.lt.reggie.service.CategoryService;
import com.lt.reggie.service.SetmealDishService;
import com.lt.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 分页查询套餐信息及其对应套餐分类名称
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page,int pageSize,String name) {

        Page<Setmeal> pageinfo = new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(name!=null,Setmeal::getName,name);
        qw.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageinfo,qw);

        BeanUtils.copyProperties(pageinfo,setmealDtoPage,"records");

        List<Setmeal> records = pageinfo.getRecords();

        List<SetmealDto> list = records.stream().map((i) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(i, setmealDto);
            Long categoryId = i.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return setmealDtoPage;
    }

    /**
     * 更新状态
     * @param status
     * @param ids
     */
    @Override
    public boolean updateStatus(String status, List ids) {
        LambdaUpdateWrapper<Setmeal> uw = new LambdaUpdateWrapper();
        uw.in(Setmeal::getId,ids).set(Setmeal::getStatus,status.equals("0")?"0":"1");
        boolean flag = this.update(uw);
        return flag;
    }

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional
    public boolean saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息
        boolean flag1 = this.save(setmealDto);

        // 保存套餐和菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> dishes = setmealDishes.stream().map((i) -> {
            i.setSetmealId(setmealDto.getId());
            return i;
        }).collect(Collectors.toList());

        boolean flag2 = setmealDishService.saveBatch(dishes);
        return flag1&&flag2;
    }

    /**
     * 删除套餐的同时删除对应的关联信息
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public boolean deleteWithDish(List<Long> ids) {
        boolean flag1 = this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.in(SetmealDish::getSetmealId,ids);
        boolean flag2 = setmealDishService.remove(qw);
        return flag1&&flag2;
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public SetmealDto getWithDishById(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(id!=null,SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(qw);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐信息，并保存到关联信息
     * @return
     */
    @Override
    @Transactional
    public boolean updateWithDish(SetmealDto setmealDto) {
        // 修改套餐信息
        boolean flag1 = this.updateById(setmealDto);
        // 修改关联信息
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(qw);

        // 把套餐里的菜品和套餐的id关联
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((i)->{
            i.setSetmealId(setmealDto.getId());
            return i;
        }).collect(Collectors.toList());

        boolean flag2 = setmealDishService.saveBatch(setmealDishes);

        return flag1&&flag2;
    }

    /**
     * 小程序套餐查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> getList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        qw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        qw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = this.list(qw);
        return list;
    }


}
