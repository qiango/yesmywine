package com.yesmywine.email.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.email.dao.EmailDao;
import com.yesmywine.email.dao.ThemeDao;
import com.yesmywine.email.entity.Email;
import com.yesmywine.email.entity.Theme;
import com.yesmywine.email.service.EmailSendService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Created by wangdiandian on 2017/5/16.
 */
@Service
public class EmailSendImpl implements EmailSendService {
    @Autowired
    private EmailDao emailDao;
    @Autowired
    private ThemeDao themeDao;

    public String send(Map<String, String> param) throws yesmywineException {
        JSONObject JsonTitle = JSONObject.parseObject(param.get("title"));
        JSONObject JsonTheme = JSONObject.parseObject(param.get("theme"));
        String code = param.get("code");
        //得到邮件账号信息
        Email email=emailDao.findAll().get(0);
        String smtpPort = email.getPort();//端口号
        String encryption = email.getEncryption();//加密类型
        String myEmailSMTPHost=email.getEmail();
        String myEmailAccount=email.getEmailLoginName();
        String myEmailPassword=email.getEmailPassword();
        //得到对应需发送的模板
        Theme theme=themeDao.findByCode(code);
        String themeTitle=theme.getTitle();
        String content=theme.getThemeTemplate();
        if(ValueUtil.notEmpity(JsonTitle)){
            Set<String> themeTitleKeySet = JsonTitle.keySet();
            for(String key : themeTitleKeySet) {
                String value = JsonTitle.get(key).toString();
                String tempContent = themeTitle.replaceAll("\\$\\{("+key+")\\}",value);
                themeTitle = tempContent;
            }
        }
        if(ValueUtil.notEmpity(JsonTheme)){
            Set<String> themeKeySet = JsonTheme.keySet();
            for(String key : themeKeySet) {
                String value = JsonTheme.get(key).toString();
                String tempContent = content.replaceAll("\\$\\{("+key+")\\}",value);
                content = tempContent;
            }
        }
        System.out.print(content);
        String receiveMailAccount=param.get("receiveMailAccount");//收件人
        try {
            createEmail(myEmailSMTPHost,myEmailAccount,myEmailPassword,receiveMailAccount,themeTitle,content,smtpPort,encryption);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
    public static void createEmail(String myEmailSMTPHost, String myEmailAccount,
                                   String myEmailPassword, String receiveMailAccount,
                                   String themeName,String content,
                                   String smtpPort,String encryption )throws Exception
    {

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置

        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");// 采用指定用户名密码的方式去认证
//        final String smtpPort = "587"465;
//        final String smtpPort = "25";
        props.setProperty("mail.smtp.socketFactory.fallback", "false");

        if(encryption.equals("ssl")){
            props.setProperty("mail.smtp.port", smtpPort);// 更改连接服务器的端口
            props.setProperty("mail.smtp.socketFactory.port", smtpPort);
            props.put("mail.smtp.ssl.enable", "true");
        }else if(encryption.equals("starttls")){
            props.setProperty("mail.smtp.port", "25");
            props.setProperty("mail.smtp.socketFactory.port", "25");
            props.setProperty("mail.smtp.starttls.enable", "true");//使用starttls加密
        }else if(encryption.equals("no")){
            props.setProperty("mail.smtp.port", "25");
            props.setProperty("mail.smtp.socketFactory.port", "25");
        }
          // 需要请求认证
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, myEmailAccount, receiveMailAccount,themeName,content);
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
        transport.connect(myEmailAccount, myEmailPassword);
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }
    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String themeName,String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "也买酒", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
//        Address[] receive = receiveMail.split(";");
        String to=receiveMail;
        InternetAddress[] toList = new InternetAddress().parse(to);
//        for (int i = 0; i <receive.length ; i++) {
//            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receive[i], null, "UTF-8"));
            message.addRecipients(MimeMessage.RecipientType.TO,toList);
//            message.addRecipients();
//        }

        // 4. Subject: 邮件主题
        message.setSubject(themeName, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）


        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
}
