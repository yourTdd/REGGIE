package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //添加购物车
    ShoppingCart add(ShoppingCart shoppingCart);

    // 通过菜品/套餐Id 修改
    boolean updateBy(ShoppingCart shoppingCart);
}
