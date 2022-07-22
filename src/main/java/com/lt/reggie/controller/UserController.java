package com.lt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.reggie.common.R;
import com.lt.reggie.entity.User;
import com.lt.reggie.entity.UserDto;
import com.lt.reggie.service.UserService;
import com.lt.reggie.utils.SMSUtils;
import com.lt.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            // 调用阿里云提供的短信服务
//            SMSUtils.sendMessage("涛堆堆","SMS_246160477",phone,code);
            // 保存验证码到session
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param userDto
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpSession session){
        log.info(userDto.toString());

        String phone = userDto.getPhone();
        String code = userDto.getCode();
        String codeInSession= (String) session.getAttribute(phone);

        if (codeInSession!=null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone,phone);
            User user = userService.getOne(qw);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
