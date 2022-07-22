package com.lt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.reggie.common.BaseContext;
import com.lt.reggie.entity.ShoppingCart;
import com.lt.reggie.mapper.ShoppingCartMapper;
import com.lt.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        // 设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        if (dishId!=null){
            // 菜品
            qw.eq(ShoppingCart::getDishId,dishId);
        }else {
            // 套餐
            qw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = this.getOne(qw);

        if (one!=null){
            // 已经存在，原来数量+1
            Integer number = one.getNumber();
            one.setNumber(number+1);
            this.updateById(one);
        }else {
            // 不存在 添加到购物车 数量默认1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            one = shoppingCart;
        }
        return one;
    }

    /**
     * 通过菜品/套餐Id 修改
     * @param shoppingCart
     * @return
     */
    @Override
    public boolean updateBy(ShoppingCart shoppingCart) {
        boolean flag = false;
        if (shoppingCart.getDishId() != null) {
            Long dishId = shoppingCart.getDishId();
            LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
            qw.eq(ShoppingCart::getDishId,dishId);
            qw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart shoppingCartOne = this.getOne(qw);
            Integer number = shoppingCartOne.getNumber();
            if (number!=1) {
                shoppingCartOne.setNumber(number-1);
                flag = this.updateById(shoppingCartOne);
            }else {
                flag = this.removeById(shoppingCartOne);
            }
        } else {
            Long setmealId = shoppingCart.getSetmealId();
            LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
            qw.eq(ShoppingCart::getSetmealId,setmealId);
            qw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart shoppingCartOne = this.getOne(qw);
            Integer number = shoppingCartOne.getNumber();
            if (number!=1){
                shoppingCartOne.setNumber(number-1);
                flag = this.updateById(shoppingCartOne);
            }else {
                flag = this.removeById(shoppingCartOne);
            }

        }
        return flag;
    }
}
