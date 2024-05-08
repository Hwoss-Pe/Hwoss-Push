package com.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@Slf4j
public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getName());

    @RequestMapping("/test")
    String test() {
//        int a = 1/0;
//        aa.builder().name("test");
        log.info("Testing");
        log.error("1111");
//        LOG.("1111");
        LOG.info("1111");
        LOG.warning("1111");
        return "nihao ";
    }

}
