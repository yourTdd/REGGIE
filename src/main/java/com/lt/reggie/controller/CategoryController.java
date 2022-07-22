package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.Category;
import com.lt.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.SplittableRandom;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> add(@RequestBody Category category){

        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper();
        // 添加排序条件
        qw.orderByAsc(Category::getSort);
        // 进行分页查询
        categoryService.page(pageInfo,qw);
        return R.success(pageInfo);
    }


    @DeleteMapping
    public R<String> delete(Long id){

        // categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("删除成功！");
    }

    /**
     * 根据id修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功成功");
    }


    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> type(Category category){
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper();
        qw.eq(category.getType()!=null,Category::getType,category.getType());
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(qw);
        return R.success(list);
    }
}
