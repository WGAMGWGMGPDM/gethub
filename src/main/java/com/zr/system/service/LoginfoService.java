package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Loginfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.LoginfoVo;

public interface LoginfoService extends IService<Loginfo>{


    DataGridView queryAllLoginfo(LoginfoVo loginfoVo);
}
