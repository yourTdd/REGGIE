package com.lt.reggie.entity;


import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    // 联系口味和菜品
    private List<DishFlavor> flavors = new ArrayList<>();

    // 联系分类和菜品
    private String categoryName;

    // 联系套餐和菜品
    private Integer copies;
}
