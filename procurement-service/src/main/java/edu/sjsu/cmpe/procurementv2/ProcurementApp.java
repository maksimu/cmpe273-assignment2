package edu.sjsu.cmpe.procurementv2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

/**
 * User: maksim
 * Date: 3/29/14 - 7:46 PM
 */
@Controller
@ComponentScan(basePackages = "edu.sjsu.cmpe.procurementv2")
@EnableAutoConfiguration
public class ProcurementApp {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ProcurementApp.class, args);
    }
}
