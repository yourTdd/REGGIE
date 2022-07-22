package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.Category;

import java.util.List;


public interface CategoryService extends IService<Category> {

     void remove(Long id);



}
