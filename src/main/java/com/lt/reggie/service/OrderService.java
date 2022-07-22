package com.lt.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lt.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    boolean submit(Orders orders);

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    Page pageWithDetail(int page, int pageSize, String number);
}
