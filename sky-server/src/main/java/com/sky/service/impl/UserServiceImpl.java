package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //判断openid是否为空
        String openId = getOpenId(userLoginDTO.getCode());

        if (openId==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断openid是否存在，即是否是新用户,是新用户，则保存完成注册
        User user = userMapper.getByOpenId(openId);

        if (user==null){
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获得openid
     * @param code
     * @return
     */
    private String getOpenId(String code){
        //调用微信接口服务，获得openid
        Map<String, String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String string = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSONObject.parseObject(string);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
