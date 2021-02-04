# springboot 邮件demo
- 简单邮件
- 复杂邮件
    - html
    - 带附件
    - 带图片
    - 模板邮件
    
## 准备工作
### 1.引入依赖
mail + thymeleaf
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
``` 

### 2.邮件配置
```yaml
spring:
  # 邮件配置
  mail:
    host: smtp.qq.com
    username: QQ邮箱账号
    password: 这个不是QQ密码，而是自己邮箱-账户-开启POP3/SMTP时的客户端授权码
    properties:
      mail:
        smtp:
          ssl:
            enable: true # 开启加密验证

##网易163邮箱
#spring:
#  mail:
#    host: smtp.163.com
#    username: 邮箱账号
#    password: 邮箱密码
#    properties:
#      mail:
#        smtp:
#          # 需要验证登录名和密码
#          auth: true
#        starttls:
#          # 需要TLS认证 保证发送邮件安全验证
#          enable: true
#          required: true
```

### 3.引入模板文件
创建文件`mailTemplate.html`放至templates文件夹
```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <base target="_blank" />
    <style type="text/css">::-webkit-scrollbar{ display: none; }</style>
    <style id="cloudAttachStyle" type="text/css">#divNeteaseBigAttach, #divNeteaseBigAttach_bak{display:none;}</style>
    <style id="blockquoteStyle" type="text/css">blockquote{display:none;}</style>
    <style type="text/css">
        body{font-size:14px;font-family:arial,verdana,sans-serif;line-height:1.666;padding:0;margin:0;overflow:auto;white-space:normal;word-wrap:break-word;min-height:100px}
        td, input, button, select, body{font-family:Helvetica, 'Microsoft Yahei', verdana}
        pre {white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;width:95%}
        th,td{font-family:arial,verdana,sans-serif;line-height:1.666}
        img{ border:0}
        header,footer,section,aside,article,nav,hgroup,figure,figcaption{display:block}
        blockquote{margin-right:0px}
    </style>
</head>
<body tabindex="0" role="listitem">
<table width="700" border="0" align="center" cellspacing="0" style="width:700px;">
    <tbody>
    <tr>
        <td>
            <div style="width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;">
                <table border="0" cellpadding="0" cellspacing="0" width="700" height="39" style="font:12px Tahoma, Arial, 宋体;">
                    <tbody><tr><td width="210"></td></tr></tbody>
                </table>
            </div>
            <div style="width:680px;padding:0 10px;margin:0 auto;">
                <div style="line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;">
                    <strong style="display:block;margin-bottom:15px;">尊敬的用户：<span style="color:#f60;font-size: 16px;"></span>您好！</strong>
                    <strong style="display:block;margin-bottom:15px;">
                        您正在进行<span style="color: red">注销账号</span>操作，请在验证码输入框中输入：<span style="color:#f60;font-size: 24px">[(${code})]</span>，以完成操作。
                    </strong>
                </div>
                <div style="margin-bottom:30px;">
                    <small style="display:block;margin-bottom:20px;font-size:12px;">
                        <p style="color:#747474;">
                            注意：此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全
                            <br>（工作人员不会向你索取此验证码，请勿泄漏！)
                        </p>
                    </small>
                </div>
            </div>
            <div style="width:700px;margin:0 auto;">
                <div style="padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;">
                    <p>此为系统邮件，请勿回复<br>
                        请保管好您的邮箱，避免账号被他人盗用
                    </p>
                    <p>院主网络科技团队</p>
                </div>
            </div>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
```

### 4.邮件工具类
```java
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
        mailSender.send(message);
        log.info("邮件发送成功...");
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

```

### 6.测试类
```java
@SpringBootTest(classes = SpringbootMailDemoApplication.class)
@RunWith(SpringRunner.class)
class SpringbootMailDemoApplicationTests {

    String to = "1423928650@qq.com";
    String content = "<mark>123456</mark>";

    @Test
    public void testSimpleMail(){
        MailUtils.sendHTMLMail(to, "一封简单邮件", content);
    }
    @Test
    public void testHTMlMail(){
        MailUtils.sendHTMLMail(to, "一封HTML邮件", content);
    }
    @Test
    public void testAttachmentMail(){
        // 准备好一个文件，在项目根目录下，名为 1.txt
        String filePath = "1.txt";
        MailUtils.sendAttachmentMail(to, "一封附件邮件", content, filePath);
    }
    @Test
    public void testInlineMail(){
        // 准备好一张图片，在项目根目录下，名为 logo.png
        String imgPath = "logo.png";
        String contentId = "img";
        content = "<img src='cid:"+contentId+"'/>";
        MailUtils.sendInlineMail(to, "一封内联图片文件", content, contentId, imgPath);
    }
    @Test
    public void testTemplateMail(){
        // 模板文件放至templates文件夹下
        MailUtils.sendTemplateMail(to, "一封模板邮件", content);
    }
}
```
   
### 7.测试结果
![](https://liuyou-images.oss-cn-hangzhou.aliyuncs.com/markdown/image-20210204081117813.png)

# 小结
通过这个demo我学到了，在项目中 
- 使用try-catch捕获异常，便于log日志处理
- 注入静态变量，可采用setter方法注入

工具类改进之处 ==> 邮件系统
- 微服务
- 异常处理
- 定时任务（重试发送）
- 异步任务