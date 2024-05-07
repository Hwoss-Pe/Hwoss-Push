package com.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Test {
    @RequestMapping("/test")
    String test(){
        aa.builder().name("test");
        log.info("1111");
        return "nihao ";
    }

}
