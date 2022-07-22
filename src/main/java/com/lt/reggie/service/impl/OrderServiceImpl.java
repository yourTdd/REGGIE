package com.lt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.reggie.common.BaseContext;
import com.lt.reggie.common.CustomException;
import com.lt.reggie.entity.*;
import com.lt.reggie.mapper.OrderMapper;
import com.lt.reggie.service.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public boolean submit(Orders orders) {

        // 获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        // 查询当前购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);

        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单！");
        }

        // 查询用户数据
        User user = userService.getById(currentId);

        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单！");
        }

        // 向订单表插入数据 一条
        long orderId = IdWorker.getId();// 订单号

        // 原子操作 多线程下线程安全
        AtomicInteger amount = new AtomicInteger(0);// 初始值

        List<OrderDetail> orderDetails= shoppingCarts.stream().map((i)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(i.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(i.getDishId());
            orderDetail.setSetmealId(i.getSetmealId());
            orderDetail.setImage(i.getImage());
            orderDetail.setDishFlavor(i.getDishFlavor());
            orderDetail.setNumber(i.getNumber());
            orderDetail.setAmount(i.getAmount());
            amount.addAndGet(i.getAmount().multiply(new BigDecimal(i.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(currentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));// 总金额
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                +(addressBook.getCityName() == null ? "" : addressBook.getCityName())
                +(addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        boolean flag1 = this.save(orders);

        // 向订单明细表插入数据 多条
        boolean flag2 = orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        boolean flag3 = shoppingCartService.remove(qw);

        return flag1&&flag2&&flag3;
    }

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @Override
    public Page pageWithDetail(int page, int pageSize, String number) {

        Page<Orders> pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(number!=null,Orders::getNumber,number);
        qw.orderByDesc(Orders::getOrderTime);

        this.page(pageInfo, qw);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> list = records.stream().map((i) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(i, ordersDto);

            Long userId = i.getUserId();
            User user = userService.getById(userId);

            Long addressBookId = i.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);

            ordersDto.setUserName(addressBook.getConsignee());
            ordersDto.setPhone(addressBook.getPhone());
            ordersDto.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);
        return ordersDtoPage;
    }
}
