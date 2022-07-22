package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.reggie.common.BaseContext;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.ShoppingCart;
import com.lt.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        qw.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(qw);
        return R.success(list);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        ShoppingCart cart = shoppingCartService.add(shoppingCart);

        return cart!=null?R.success(cart):R.error("添加失败");
    }

    /**
     * 通过菜品/套餐Id 修改
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> update(@RequestBody ShoppingCart shoppingCart){
        boolean flag = shoppingCartService.updateBy(shoppingCart);
        return flag?R.success("修改成功"):R.error("修改失败");
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        boolean flag = shoppingCartService.remove(qw);
        return flag?R.success("删除成功"):R.error("删除失败");
    }
}
