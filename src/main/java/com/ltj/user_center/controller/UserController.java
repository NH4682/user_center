package com.ltj.user_center.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.user_center.common.BaseResponse;
import com.ltj.user_center.common.ErrorCode;
import com.ltj.user_center.common.ResultUtils;
import com.ltj.user_center.exception.BusinessException;
import com.ltj.user_center.model.User;
import com.ltj.user_center.model.domain.request.UserLoginRequest;
import com.ltj.user_center.model.domain.request.UserRegisterRequest;
import com.ltj.user_center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ltj.user_center.contant.UserConstant.USER_LOGIN_STATE;



@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 注册
     * @param userRegisterRequest 注册信息
     * @return 用户id
     */
    @PostMapping("/register")
    BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String verifyPassword = userRegisterRequest.getVerifyPassword();
        //输入值是否为空，null, 空格
        if (StringUtils.isAnyBlank(userAccount, userPassword, verifyPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long id = userService.userRegister(userAccount, userPassword, verifyPassword);
        return ResultUtils.success(id);
    }

    /**
     * 登录
     * @param userLoginRequest 登录信息类
     * @param request 请求信息
     * @return 登录对象
     */
    @PostMapping("/login")
    BaseResponse<User> userLongin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //输入值是否为空，null, 空格
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLongin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 查询用户
     * @param userName 用户名
     * @param request
     * @return 用户数据集合
     */
    @GetMapping("/search")
    BaseResponse<List<User>> Search( @RequestParam String userName ,HttpServletRequest request){
        Boolean isAdmin = userService.authentication(request);
        if (!isAdmin){
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        List<User> userList = userService.Search(userName, request);
        return ResultUtils.success(userList);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @param request
     * @return 是否删除成功 true 或 false
     */
    @PostMapping("/delete")
    BaseResponse<Boolean> delete(@RequestBody int id,HttpServletRequest request){
        Boolean isAdmin = userService.authentication(request);
        if (!isAdmin){
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"ID参数不存在");
        }
        Boolean aBoolean = userService.delete(id, request);
        return ResultUtils.success(aBoolean);
    }

    /**
     * 将已登录的用户信息返回给前端
     * @param request
     * @return
     */
    @GetMapping("/currentUser")
    BaseResponse<User>  currentUser(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        User userServiceById = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(userServiceById);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/outLogin")
    BaseResponse<Integer> outLogin( HttpServletRequest request){
        Integer outLogin = userService.outLogin(request);
        return ResultUtils.success(outLogin);
    }

    /**
     *  通过标签搜索用户
     * @param tagList 标签列表
     * @return 用户列表
     */
    @GetMapping("/search/tags")
    BaseResponse<List<User>> SearchTags(@RequestParam(required = false) List<String> tagList){
        System.out.println(tagList);
        if (CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagList);
        return ResultUtils.success(userList);
    }

    /**
     * 修改用户信息
     * @param user 要修改的信息 user:{id：1，phone:189079879}
     * @return
     */
    @PostMapping("/updateUser")
    BaseResponse<Boolean> updateUser(@RequestBody User user){
        Boolean aBoolean = userService.updateUser(user);
        //未登录用户
        if (!aBoolean){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return ResultUtils.success(aBoolean);
    }


    /**
     * 推荐页面
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User logininUser = userService.getLogininUser(request);
        String redisKey = String.format("ltj:user:recommend:%s",logininUser.getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //如果有缓存，直接读取
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null){
            return ResultUtils.success(userPage);
        }
        //无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        //写缓存,10s过期
        try {
            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            log.error("redis set key error",e);
        }
        return ResultUtils.success(userPage);
    }







}
