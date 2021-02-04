package xyz.liuyou;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.liuyou.utils.MailUtils;

@SpringBootTest(classes = SpringbootMailDemoApplication.class)
@RunWith(SpringRunner.class)
class SpringbootMailDemoApplicationTests {

    String to = "xxxx@qq.com";
    String content = "<mark>123456</mark>";

    @Test
    public void testSimpleMail(){
        MailUtils.sendSimpleMail(to, "一封简单邮件", content);
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
