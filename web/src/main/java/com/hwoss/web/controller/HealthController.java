package com.hwoss.web.controller;

import com.hwoss.suport.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@Slf4j
public class HealthController {

    @RequestMapping("/test")
    String test() {

        return "success ";

    }

}
