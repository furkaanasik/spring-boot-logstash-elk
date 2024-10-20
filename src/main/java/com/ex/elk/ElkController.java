package com.ex.elk;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/elk")
public class ElkController {

    Logger logger = Logger.getLogger(ElkController.class.getName());

    @RequestMapping
    public ResponseEntity<String> test() {
        logger.info("ELK INFO TEST");
        logger.warning("ELK WARNING TEST");
        logger.severe("ELK SEVERE TEST");
        return ResponseEntity.ok("ELK Test");
    }

}
