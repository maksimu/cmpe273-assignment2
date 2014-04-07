package edu.sjsu.cmpe.procurementv2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * User: maksim
 * Date: 3/29/14 - 9:38 PM
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AppConfigContext {

    @Bean
    public RestTemplate restTemplate(){

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

//    @Bean
//    public StompService stompService() throws JMSException {
//        StompService stompService = new StompService();
//        return stompService;
//    }
}
