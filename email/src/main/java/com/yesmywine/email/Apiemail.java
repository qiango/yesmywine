package com.yesmywine.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@SpringBootApplication
public class Apiemail {
    public static void main(String[] args) {
        SpringApplication.run(Apiemail.class, args);
    }
}
//public class Apiemail implements EmbeddedServletContainerCustomizer {
//    public static void main(String[] args) {
//        SpringApplication.run(Apiemail.class,args);
//    }
//    @Override
//    public void customize(ConfigurableEmbeddedServletContainer container) {
//        container.setPort(8443);
//    }
//}