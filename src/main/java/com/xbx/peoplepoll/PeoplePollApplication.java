package com.xbx.peoplepoll;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xbx.peoplepoll.mapper")
public class PeoplePollApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeoplePollApplication.class, args);
    }

}
