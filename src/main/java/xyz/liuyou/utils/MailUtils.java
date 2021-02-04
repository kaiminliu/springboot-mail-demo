package xyz.liuyou.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.FileSystem;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/2/4 5:33
 * @decription 邮件工具类
 **/
@Component
public class MailUtils {
    private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

    // 静态变量从setter方法注入
    private static JavaMailSenderImpl mailSender;

    private static TemplateEngine templateEngine;

    private static String sender;

    /**
     * 简单邮件发送
     * @date 2021/1/26 21:27
     * @param to 接收方邮箱
     * @param subject 邮件主题
     * @param text 邮件内容
     * @return boolean
     **/
    public static boolean sendSimpleMail(String to, String subject, String text) {
        log.info("简单邮件发送中... sendSipleMail: {}=={},{}==>{}", sender, subject, text, to);
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(sender);
            message.setTo(to);
        } catch (Exception e) {
            log.error("邮件发送异常：Exception : {}", e);
            return false;
        }
        mailSender.send(message); // 发送
        log.info("邮件发送成功，message : {}", message);
        return true;
    }

    /**
     * html邮件发送
     * @date 2021/1/26 21:33
     * @param to 接收方邮箱
     * @param subject 邮件主题
     * @param text 邮件内容(可以是html)
     * @return boolean
     **/
    public static boolean sendHTMLMail(String to, String subject, String text) {
        log.info("HTML邮件发送中... sendHTMLMail: {}=={},{}==>{}", sender, subject, text, to);
        MimeMessage message = mailSender.createMimeMessage();
        // 组装
        MimeMessageHelper helper = new MimeMessageHelper(message);
//        MimeMessageHelper helper = new MimeMessageHelper(message,true);// 多文本上传（附件）
//        MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");// 编码
        try {
            helper.setSubject(subject);
            helper.setText(text,true);
            helper.setFrom(sender);
            helper.setTo(to);
        } catch (MessagingException e) {
            log.error("邮件发送异常：Exception : {}", e);
            return false;
        }
        mailSender.send(message);
        log.info("邮件发送成功...");
        return true;
    }

    /**
     * 发送带附件的邮件
     * @date 2021/2/4 5:55
     * @param to
     * @param subject
     * @param text
     * @param attachmentPath 附件路径
     * @return boolean
     **/
    public static boolean sendAttachmentMail(String to, String subject, String text, String attachmentPath) {
        log.info("附件邮件发送中... sendAttachmentMail: {}=={},{} (附件路径：{})==>{}", sender, subject, text, attachmentPath, to);
        MimeMessage message = mailSender.createMimeMessage();
        FileSystemResource resource = new FileSystemResource(new File(attachmentPath));
        MimeMessageHelper helper = null;// 文本上传（附件）
        String filename = resource.getFilename();
        try {
            helper = new MimeMessageHelper(message,true);
            helper.setSubject(subject);
            helper.setText(text,true);
            helper.addAttachment(filename, resource); // 添加附件
            helper.setFrom(sender);
            helper.setTo(to);
        } catch (MessagingException e) {
            log.error("邮件发送异常：Exception : {}", e);
            return false;
        }
        log.info("邮件发送成功...");
        mailSender.send(message);
        return true;
    }

    /**
     * 发送图片邮件（图片以内联方式内嵌html,即可通过img标签在线显示）
     * html格式 <img src='cid:'+contentId />
     * @date 2021/2/4 6:22
     * @param to
     * @param subject
     * @param text
     * @param contentId 内容id
     * @param imgPath
     * @return boolean
     **/
    public static boolean sendInlineMail(String to, String subject, String text, String contentId, String imgPath) {
        log.info("图片邮件发送中... sendInlineMail: {}=={},{} (cid: {},内联图片路径：{})==>{}", sender, subject, text, contentId, imgPath, to);
        MimeMessage message = mailSender.createMimeMessage();
        FileSystemResource resource = new FileSystemResource(imgPath);
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setText(text,true);
            helper.addInline(contentId, resource); // 添加内联图片
            helper.setFrom(sender);
            helper.setTo(to);
        } catch (MessagingException e) {
            log.error("邮件发送异常：Exception : {}", e);
            return false;
        }
        mailSender.send(message);
        log.info("邮件发送成功...");
        return true;
    }

    /**
     * 模板邮件 （模板引擎采用thymeleaf）
     * @date 2021/2/4 6:34
     * @param to
     * @param subject
     * @param text （表示需要替换的内容，按实际来） 这样的参数可以有多个
     * @return boolean
     **/
    public static boolean sendTemplateMail(String to, String subject, String text) {
        Context context = new Context(); // org.thymeleaf.context
        context.setVariable("code", text);
        String content = templateEngine.process("mailTemplate.html", context);
        return sendHTMLMail(to, subject, content); // 使用html邮件发送
    }

    @Autowired(required = false)
    public void setTemplateEngine(TemplateEngine templateEngine) {
        MailUtils.templateEngine = templateEngine;
    }

    @Autowired(required = false)
    public void setMailSender(JavaMailSenderImpl mailSender) {
        MailUtils.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    public void setSender(String sender) {
        MailUtils.sender = sender;
    }
}

