package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Useraddr;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
* @Author: 张忍
* @Date: 2020-05-19 19:18
*/
public interface UseraddrService extends IService<Useraddr>{

        DataGridView loadUserAddr();

        void incrementAddrNum(String substring);

        void decrementAddrNum(String substring);
    }
