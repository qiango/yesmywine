package com.yesmywine.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by hz on 12/8/16.
 */
@SpringBootApplication
@EnableScheduling
public class ApiShipper implements EmbeddedServletContainerCustomizer {
    public static void main(String[] args) {
        SpringApplication.run(ApiShipper.class,args);
    }
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(8080);
    }
}

