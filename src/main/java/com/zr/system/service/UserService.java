package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.UserVo;

import java.io.IOException;

public interface UserService extends IService<User> {
    /**
     * 根据用户名查询用户信息
     * @param loginname
     * @return
     */
    User queryUserByLoginName(String loginname);

    User queryUserByOpenId(String openId);

    User queryUserByLoginNameIgnoreAvailable(String loginname);

    DataGridView queryAllUser(UserVo userVo);

    User updateUser(User user);

    User saveUser(User user) throws IOException;

    Integer queryUserMaxOrderNum();

    User queryUserByCdkey(String cdkey);

    void savaUserRole(Integer uid, Integer[] rids);

    void followUser(Integer uid, Integer id) throws IOException;

    void unFollowUser(Integer uid, Integer id) throws IOException;

    ResultObj queryFollow(Integer uid, Integer id);

    DataGridView loadFollowUser(UserVo userVo);

    Integer queryFollowCountByUid(Integer uid);



}

