package com.ltj.user_center.service;

import com.ltj.user_center.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 24095
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-08-14 20:56:10
 */
public interface UserService extends IService<User> {
    final String SALT = "lTJ";

    /**
     * 注册
     *
     * @param userAccount    用户账号
     * @param userPassword   用户密码
     * @param verifyPassword 校验密码
     * @return 用户ID
     */
    long userRegister(String userAccount, String userPassword, String verifyPassword);

    /**
     * 登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户对象
     */
    User userLongin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 查找
     *
     * @param userName 用户名
     * @param request  请求信息
     * @return 用户数据集合
     */
    List<User> Search(String userName, HttpServletRequest request);

    /**
     *
     * @param id 用户ID
     * @param request 请求
     * @return Boolean
     */
    Boolean delete(int id, HttpServletRequest request);

    /**
     * 鉴权（判断是否为管理员）
     *
     * @param request
     * @return
     */
    Boolean authentication(HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originUser 用户类
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户退出
     * @param request
     * @return
     */
    Integer outLogin(HttpServletRequest request);


     List<User> searchUsersByTags(List<String> tagNameList);

     Boolean updateUser(User user);

     User getLogininUser(HttpServletRequest request);

}
