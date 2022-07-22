package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.Employee;
import com.lt.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController{

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1、将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3、如果没有查询到就返回登录失败结构
        if (emp == null){
            return R.error("登录失败！");
        }

        // 4、密码比对
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败！");
        }

        // 5、查看员工状态,是否禁用
        if (emp.getStatus() == 0){
            return R.error("账号已禁用！");
        }

        // 6、登录成功 将员工id存入session并返回登录结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工，员工信息："+employee);

        // 设置初始密码123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper();
        // 添加过滤条件
        qw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 添加排序条件
        qw.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo,qw);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request){

        Long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到该员工信息");
    }

}
