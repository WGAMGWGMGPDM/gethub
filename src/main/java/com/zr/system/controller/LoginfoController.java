package com.zr.system.controller;

import com.zr.system.common.ResultObj;
import com.zr.system.service.LoginfoService;
import com.zr.system.vo.LoginfoVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/loginfo")
public class LoginfoController {
    @Autowired
    private LoginfoService loginfoService;

    /**
     * 加载所有登录日志
     * @param loginfoVo
     * @return
     */
    @RequestMapping("loadAllLoginfo")
    public Object loadAllLoginfo(LoginfoVo loginfoVo){
        return this.loginfoService.queryAllLoginfo(loginfoVo);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("deleteLoginfo")
    @RequiresPermissions("loginfo:delete")
    public ResultObj deleteLoginfo(Integer id){
        try {
            this.loginfoService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("batchDeleteLoginfo")
    @RequiresPermissions("loginfo:batchDelete")
    public ResultObj batchDeleteLoginfo(Integer[] ids){

        try {
            if (null!=ids&&ids.length>0){
                List<Integer> idList = new ArrayList<>();
                for (Integer id : ids) {
                    idList.add(id);
                }
                this.loginfoService.removeByIds(idList);
                return ResultObj.DELETE_SUCCESS;
            }else {
                return new ResultObj(-1,"传入id为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}
