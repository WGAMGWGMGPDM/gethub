package com.zr.system.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {


    public static final ResultObj IS_LOGIN = new ResultObj(200,"已登陆");
    public static final ResultObj UN_LOGIN = new ResultObj(-1,"未登录");

    public static final ResultObj DELETE_SUCCESS = new ResultObj(200,"删除成功");
    public static final ResultObj DELETE_ERROR = new ResultObj(-1,"删除失败");

    public static final ResultObj UPDATE_SUCCESS = new ResultObj(200,"更新成功");
    public static final ResultObj UPDATE_ERROR = new ResultObj(-1,"更新失败");

    public static final ResultObj ADD_SUCCESS = new ResultObj(200,"添加成功");
    public static final ResultObj ADD_ERROR = new ResultObj(-1,"添加失败");

    public static final ResultObj RELEASE_SUCCESS = new ResultObj(200,"发布成功");
    public static final ResultObj RELEASE_ERROR = new ResultObj(-1,"发布失败");

    public static final ResultObj TEMP_SUCCESS = new ResultObj(200,"暂存成功");
    public static final ResultObj TEMP_ERROR = new ResultObj(-1,"暂存失败");

    public static final ResultObj DISPATCH_SUCCESS = new ResultObj(200,"分配成功");
    public static final ResultObj DISPATCH_ERROR = new ResultObj(-1,"分配失败");

    public static final ResultObj EXIST_TRUE = new ResultObj(200,"用户已存在");
    public static final ResultObj EXIST_FALSE = new ResultObj(-1,"用户未存在");

    public static final ResultObj ACTIVATE_SUCCESS = new ResultObj(200,"激活成功");
    public static final ResultObj ACTIVATE_ERROR = new ResultObj(-1,"激活失败");
    public static final ResultObj REGISTE_SUCCESS = new ResultObj(200,"注册成功");
    public static final ResultObj REGISTE_ERROR = new ResultObj(-1,"注册失败");

    public static final ResultObj SEND_SUCCESS = new ResultObj(200,"发送成功");
    public static final ResultObj SEND_ERROR = new ResultObj(-1,"发送失败");

    public static final ResultObj SOLVE_SUCCESS = new ResultObj(200,"结单成功");
    public static final ResultObj SOLVE_ERROR = new ResultObj(-1,"结单失败");

    public static final ResultObj SUBMIT_SUCCESS = new ResultObj(200,"提交成功");
    public static final ResultObj SUBMIT_ERROR = new ResultObj(-1,"提交失败");

    public static final ResultObj VALIDITY_ERROR = new ResultObj(-1,"验证码错误");

    public static final ResultObj DOWNLOAD_SUCCESS = new ResultObj(200,"下载成功");
    public static final ResultObj DOWNLOAD_ERROR = new ResultObj(-1,"下载失败");

    public static final ResultObj ARCHIVE_SUCCESS = new ResultObj(200,"存档成功");
    public static final ResultObj ARCHIVE_ERROR = new ResultObj(-1,"存档失败");

    public static final ResultObj FOLLOW_SUCCESS = new ResultObj(200,"关注成功");
    public static final ResultObj FOLLOW_ERROR = new ResultObj(-1,"关注失败");

    public static final ResultObj UNFOLLOW_SUCCESS = new ResultObj(200,"取关成功");
    public static final ResultObj UNFOLLOW_ERROR = new ResultObj(-1,"取关失败");

    public static final ResultObj STAR_SUCCESS = new ResultObj(200,"标星成功");
    public static final ResultObj STAR_ERROR = new ResultObj(-1,"标星失败");

    public static final ResultObj UNSTAR_SUCCESS = new ResultObj(200,"取标成功");
    public static final ResultObj UNSTAR_ERROR = new ResultObj(-1,"取标失败");

    public static final ResultObj FOLLOW_TRUE = new ResultObj(200,"已关注");
    public static final ResultObj FOLLOW_FALSE = new ResultObj(-1,"未关注");

    public static final ResultObj STAR_TRUE = new ResultObj(200,"已标星");
    public static final ResultObj STAR_FALSE = new ResultObj(-1,"未标星");

    public static final ResultObj BOND_SUCCESS = new ResultObj(200,"绑定成功");
    public static final ResultObj BOND_ERROR = new ResultObj(-1,"绑定失败");

    private Integer code = 200;
    private String msg = "";
    private Object data = "";


     public ResultObj(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
