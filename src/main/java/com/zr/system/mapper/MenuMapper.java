package com.zr.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.system.domain.Menu;

public interface MenuMapper extends BaseMapper<Menu> {
    Integer queryMenuMaxOrdernum();

    Integer queryMenuChildrenCountById(Integer id);
}