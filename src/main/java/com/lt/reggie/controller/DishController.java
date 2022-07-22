package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.Category;
import com.lt.reggie.entity.Dish;
import com.lt.reggie.entity.DishDto;
import com.lt.reggie.service.CategoryService;
import com.lt.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;


    @Autowired
    private CategoryService categoryService;

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

            Page<Dish> pageInfo = new Page<>(page, pageSize);

        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.like(name!=null,Dish::getName,name);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,lqw);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((i) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(i, dishDto);

            Long categoryId = i.getCategoryId();// 分类id
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if ( category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.save(dishDto);
        return R.success("添加菜品成功！");
    }

    /**
     * 根据id来查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("保存成功！");
    }


    /**
     * 删除和批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam(value = "ids")List<Long> ids){
        dishService.removeByIds(ids);
        return R.success("删除菜品成功！");
    }


    /**
     * 批量 启售/停售
     * @param stauts
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") String stauts,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Dish> uw = new LambdaUpdateWrapper();
        uw.in(Dish::getId,ids).set(Dish::getStatus,stauts.equals("0")?"0":"1");
        dishService.update(uw);
        return R.success("操作成功！");
    }

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishes = dishService.get(dish);
        return R.success(dishes);
    }

}
