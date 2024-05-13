package com.web;

import com.hwoss.suport.mq.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@Slf4j
public class Test {
    private static final Logger LOG = Logger.getLogger(Test.class.getName());
    @Autowired
    private MqService mqService;


    @RequestMapping("/test")
    String test() {
//        int a = 1/0;
        mqService.send("hwoss_KEY", "111", "1");
        return "nihao ";

    }

}
