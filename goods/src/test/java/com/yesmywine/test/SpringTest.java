//package com.hzbuvi.test;
//
//import DBConfig;
//import TestRedisBiz;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * Created by WANG, RUIQING on 12/21/16
// * Twitter : @taylorwang789
// * E-mail : i@wrqzn.com
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {DBConfig.class})
//public class SpringTest {
//
//    //    @Autowired
////    private TestBiz testBiz;
//    @Autowired
//    private TestRedisBiz userService;
//
//    @Test
//    public void test() {
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        // ValueUtil.toJson(testBiz.findAll());
//    }
//
//
//    @Test
//    public void testaa() {
////		UserService userService = (UserService) context.getBean("userService");
//
//        System.out.println("第一次执行查询：" + userService.queryFullNameById(110L));
//        System.out.println("----------------------------------");
//
//        System.out.println("第二次执行查询：" + userService.queryFullNameById(110L));
//        System.out.println("----------------------------------");
//
//        userService.deleteById(110L);
//        System.out.println("----------------------------------");
//
//        System.out.println("清除缓存后查询：" + userService.queryFullNameById(110L));
//        System.out.println("----------------------------------");
//
//        System.out.println(userService.modifyFullNameById(110L, "ZhangJunBao"));
//        System.out.println("----------------------------------");
//
//        System.out.println("修改数据后查询：" + userService.queryFullNameById(110L));
//        System.out.println("----------------------------------");
//
//        System.out.println("第一次执行查询：" + userService.queryFullNameById(112L));
//        System.out.println("----------------------------------");
//
//        System.out.println("第二次执行查询：" + userService.queryFullNameById(112L));
//        System.out.println("----------------------------------");
//    }
//}
