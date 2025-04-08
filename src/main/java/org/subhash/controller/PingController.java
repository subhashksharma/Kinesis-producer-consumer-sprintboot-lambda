package org.subhash.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.subhash.kinesis.client.service.KinesisService;

import java.util.HashMap;
import java.util.Map;


@RestController
@EnableWebMvc
public class PingController {


    @Autowired
    KinesisService kinesisService;

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() throws InterruptedException {
        Map<String, String> pong = new HashMap<>();

        kinesisService.produceData();
        pong.put("pong", "Hello, World!");
        return pong;
    }
}
