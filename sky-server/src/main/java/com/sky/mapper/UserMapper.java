package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据ID来查询用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid =#{openId}")
    User getByOpenId(String openId);

    /**
     * 插入数据
     * @param user1
     */
    void insert(User user1);

    @Select("select * from user where id=#{userId}")
    User getById(Long userId);
}
