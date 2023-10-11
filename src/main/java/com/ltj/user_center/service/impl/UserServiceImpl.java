package com.ltj.user_center.service.impl;

import java.util.ArrayList;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.user_center.common.ErrorCode;
import com.ltj.user_center.exception.BusinessException;
import com.ltj.user_center.model.User;
import com.ltj.user_center.service.UserService;
import com.ltj.user_center.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ltj.user_center.contant.UserConstant.ADMIN_ROLE;
import static com.ltj.user_center.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 24095
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-08-14 20:56:10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {


    @Resource
    UserMapper userMapper;

    /**
     * 注册
     *
     * @param userAccount    用户名
     * @param userPassword   用户密码
     * @param verifyPassword 校验密码
     * @return 用户ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String verifyPassword) {
        //包含 ""、null、" " 都为真
        //判断 用户名 用户密码 校验密码 非空 非空格 非null
        if (StringUtils.isAnyBlank(userAccount, userPassword, verifyPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        //判断用户账号长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号长度小于4位");
        }

        //判断用户密码长度不小于8位
        if (userPassword.length() < 8 ) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"密码长度小于8位");
        }

        //禁止包含用户账号包含特殊字符
        String specialCharacters = "!@#$%^&*()\\-=_+{}[\\]|:;\"'<>,.?/";
        String regex = "[" + Pattern.quote(specialCharacters) + "]";
        if (Pattern.compile(regex).matcher(userAccount).find()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号能包含特殊字符");
        }

        //校验密码于密码是否一致
        if (!userPassword.equals(verifyPassword)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"密码是否一致");
        }

        //用户账号名是否唯一
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        Integer count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_REPEAT);
        }

        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //添加用户（注册）
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        boolean save = this.save(user);

        //判断一下是否添加成功，如果添加失败user.getId()会返回null,long接受不了null
        if (!save) {
            throw new BusinessException("数据未添加成功","40010","");
        }
        //返回用户id
        return user.getId();
    }

    /**
     * 登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户对象
     */
    @Override
    public User userLongin(String userAccount, String userPassword, HttpServletRequest request) {
        //包含 ""、null、" " 都为真
        //判断 用户名 用户密码 校验密码 非空 非空格 非null
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        //判断用户账号长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }

        //判断用户密码长度不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }

        //禁止包含用户账号包含特殊字符
        String specialCharacters = "!@#$%^&*()\\-=_+{}[\\]|:;\"'<>,.?/";
        String regex = "[" + Pattern.quote(specialCharacters) + "]";
        if (Pattern.compile(regex).matcher(userAccount).find()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }

        //通过用户名查找用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_UNREGISTERED);
        }

        //输入的密码加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //输入的密码与数据库密码做比较
        if (!newPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"密码输入错误");
        }

        //脱敏
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setUserAccount(user.getUserAccount());
        newUser.setAvatarUrl(user.getAvatarUrl());
        newUser.setGender(user.getGender());
        newUser.setPhone(user.getPhone());
        newUser.setEmail(user.getEmail());
        newUser.setUserStatus(user.getUserStatus());
        newUser.setCreateTime(new Date());
        newUser.setRole(user.getRole());
        request.getSession().setAttribute(USER_LOGIN_STATE, newUser);

        return newUser;
    }



    /**
     * 查找
     *
     * @param userName 用户名
     * @param request  请求信息
     * @return 用户数据集合
     */
    @Override
    public List<User> Search(String userName, HttpServletRequest request) {
        //判断是否为管理员
        if (!authentication(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getUserAccount, userName);
        List<User> userList = userMapper.selectList(queryWrapper);
        //数据脱敏并返回
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     *
     * @param id 用户ID
     * @param request 请求
     * @return Boolean
     */
    @Override
    public Boolean delete(int id, HttpServletRequest request) {
        if (!authentication(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        if (id < 0){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        int nub = userMapper.deleteById(id);
        return nub > 0;
    }

    /**
     * 鉴权（判断是否为管理员）
     *
     * @param request
     * @return
     */
    @Override
    public Boolean authentication(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(USER_LOGIN_STATE);
        //判断是否为管理员
        if (user == null || user.getRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    public Boolean authentication(User loginUser) {
        //判断是否为管理员
        if (loginUser == null || loginUser.getRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }


    /**
     * 用户信息脱敏
     * @param originUser 用户类
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setRole(originUser.getRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @Override
    public Integer outLogin(HttpServletRequest request) {
        User user =(User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //拼接tag
        // like '%Java%' and like '%Python%'
        for (String tagList : tagNameList) {
            queryWrapper = queryWrapper.like(User::getTags, tagList);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return  userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public User getLogininUser(HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        User userServiceById = userMapper.selectById(userId);
        User safetyUser = getSafetyUser(userServiceById);
        return safetyUser;
    }


}




