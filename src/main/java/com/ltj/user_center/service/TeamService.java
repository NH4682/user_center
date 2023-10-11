package com.ltj.user_center.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ltj.user_center.model.Team;
import com.ltj.user_center.model.User;
import com.ltj.user_center.model.domain.request.TeamJoinRequest;
import com.ltj.user_center.model.domain.request.TeamQuitRequest;
import com.ltj.user_center.model.domain.request.TeamUpdateRequest;
import com.ltj.user_center.model.dto.TeamQuery;
import com.ltj.user_center.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 24095
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-10-07 21:38:34
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
   List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);


    /**
     * 修改（更新）队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);


    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    long countTeamUserByTeamId(long teamId);
}
