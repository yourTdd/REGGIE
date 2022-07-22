package com.lt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.reggie.entity.Category;
import com.lt.reggie.entity.Dish;
import com.lt.reggie.entity.DishDto;
import com.lt.reggie.entity.DishFlavor;
import com.lt.reggie.mapper.DishMapper;
import com.lt.reggie.service.CategoryService;
import com.lt.reggie.service.DishFlavorService;
import com.lt.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品信息到菜品表
        this.save(dishDto);
        Long dishId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((i)->{
            i.setDishId(dishId);
            return i;
        }).collect(Collectors.toList());

        // 保存口味数据到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id来查询菜品的信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 对应的口味，从dish_flavor中查
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(qw);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应的口味数据  dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(qw);

        // 添加当前提交过来的口味数据  dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((i)->{
            i.setDishId(dishDto.getId());
            return i;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品
     * @param dish
     * @return
     */
//    @Override
//    public List<Dish> get(Dish dish){
//        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
//        qw.eq(Dish::getStatus,1);
//        qw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        qw.eq(dish.getName()!=null,Dish::getName,dish.getName());
//
//        // 查询启售的菜品
//        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = this.list(qw);
//        return list;
//    }
    // 小程序改造
    @Override
    public List<DishDto> get(Dish dish){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getStatus,1);
        qw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        qw.eq(dish.getName()!=null,Dish::getName,dish.getName());

        // 查询启售的菜品
        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = this.list(qw);

        List<DishDto> dishDtoList = list.stream().map((i) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(i, dishDto);

            // 追加分类名
            Long categoryId = i.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if ( category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 追加口味信息
            Long dishId = i.getId();// 当前菜品id
            LambdaQueryWrapper<DishFlavor> qw1 = new LambdaQueryWrapper<>();
            qw1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(qw1);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }
}
