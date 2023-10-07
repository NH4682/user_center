package com.ltj.user_center.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.user_center.mapper.TeamMapper;
import com.ltj.user_center.model.Team;
import com.ltj.user_center.service.TeamService;
import org.springframework.stereotype.Service;

/**
* @author 24095
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-10-07 21:38:34
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




