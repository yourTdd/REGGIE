package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.Orders;
import com.lt.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        boolean flag = orderService.submit(orders);
        return flag?R.success("下单成功！"):R.error("下单失败！");
    }

    /**
     *  小程序分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(Orders::getOrderTime);

        Page orderPage = orderService.page(pageInfo);

        return R.success(orderPage);
    }

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> page (int page,int pageSize,String number){
        Page orderPage = orderService.pageWithDetail(page, pageSize, number);
        return R.success(orderPage);
    }
}
