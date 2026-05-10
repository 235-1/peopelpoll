package com.xbx.back.controller;

import com.xbx.back.mapper.ProjectMemMapper;
import com.xbx.back.pojo.ProjectMem;
import com.xbx.back.pojo.Result;
import com.xbx.back.service.ProjectMemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author X
 * @date 2026/4/23 22:22
 */
@Slf4j
@RestController
public class ProjectMemController {
    @Autowired
    private ProjectMemService projectMemService;
    // 查询所有
    @GetMapping("/projectMem")
    public Result getAll() {
        log.info("查询所有");
        List<ProjectMem> projectMems = projectMemService.getAll();
        Result result = projectMems.size() > 0 ? Result.success(projectMems) : Result.error();
        return result;
    }

}
