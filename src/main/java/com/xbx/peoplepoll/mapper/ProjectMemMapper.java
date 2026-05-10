package com.xbx.back.mapper;


import com.xbx.back.pojo.ProjectMem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMemMapper {


    List<ProjectMem> getAll();
}
