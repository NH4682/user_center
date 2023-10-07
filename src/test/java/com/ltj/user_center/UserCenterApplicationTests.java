package com.ltj.user_center;

import java.util.Date;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.ltj.user_center.mapper.UserMapper;
import com.ltj.user_center.model.User;
import com.ltj.user_center.service.UserService;
import com.ltj.user_center.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

    @Test
    void contextLoads2() {
        User user = new User();
        user.setUsername("小明");
        user.setUserAccount("xiaoming");
        user.setAvatarUrl("https://img1.baidu.com/it/u=2418939734,3670115971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=501");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("15678690809");
        user.setEmail("123@qq.com");
        user.setUserStatus(0);
        user.setIsDelete(0);
        userService.save(user);

    }

}
