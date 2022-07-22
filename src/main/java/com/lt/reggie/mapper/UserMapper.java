package com.lt.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
