package com.yesmywine.user.controller;

import com.yesmywine.user.dao.AdminUserDao;
import com.yesmywine.user.entity.AdminUser;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ${shuang} on 2017/6/29.
 */
@RestController
@RequestMapping("/user/admin/itf")
public class AdminUserController {

    @Autowired
    private AdminUserDao adminUserDao;

    @RequestMapping(method = RequestMethod.POST)
    public String localGenerate(String uid){
        try {
            ValueUtil.verify(uid);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
        AdminUser newAdminUser = adminUserDao.findByUid(uid);
        if(ValueUtil.notEmpity(newAdminUser)){
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"该用户已经存在");
        }else {
            AdminUser adminUser = new AdminUser();
            adminUser.setUid(uid);
            adminUserDao.save(adminUser);
            return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
        }
    }


}
