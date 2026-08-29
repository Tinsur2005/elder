package cn.tinsur.elder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * JavaMailSender 发送邮件 DEMO
 * 需要先在 application.yml 里配置 spring.mail 信息（host/用户名/授权码）
 * 然后直接运行下面的测试方法即可发送邮件
 */
@SpringBootTest
class ElderApplicationTests {

    // 自动注入Spring容器中由 spring-boot-starter-mail 自动装配好的 JavaMailSender
    @Autowired
    private JavaMailSender mailSender;

    /**
     * DEMO1：发送一封最简单的纯文本邮件
     * SimpleMailMessage：Spring 提供的简单邮件对象，只能发纯文本，不能带附件/HTML
     */
    @Test
    void sendSimpleMail() {
        // 1.创建一封简单的邮件消息对象（new一个空对象）
        SimpleMailMessage message = new SimpleMailMessage();
        // 2.设置发件人（要和 yml 里配置的 username 一致）
        message.setFrom("@qq.com");
        // 3.设置收件人（可以写多个，逗号隔开）
        message.setTo("@qq.com");
        // 4.设置邮件主题
        message.setSubject("这是一封测试邮件");
        // 5.设置邮件正文（纯文本）
        message.setText("你好，这是使用 Spring Boot JavaMailSender 发送的第一封邮件！");
        // 6.调用 send 方法真正发出
        mailSender.send(message);
        // 注意：测试方法跑完如果没抛异常，就说明发送成功
    }

    /**
     * DEMO2：发送支持附件、内联图片的复杂邮件
     * MimeMessageHelper：底层 mail 对象，可发 HTML、带附件
     */
    @Test
    void sendMimeMail() throws MessagingException {
        // 1.创建底层 MimeMessage（封装真正的SMTP报文）
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 2.用 MimeMessageHelper 来填充内容，true 表示允许携带附件/多部分
        //   这里不指定编码，会读取 yml 里配置的 default-encoding: UTF-8
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        // 3.分别设置发件人、收件人、主题
        helper.setFrom("你的QQ邮箱@qq.com");
        helper.setTo("收件人邮箱@xx.com");
        helper.setSubject("带附件的测试邮件");
        // 4.支持 HTML 正文（第二个参数传 true 表示按HTML解析）
        helper.setText("<h1>你好</h1><p style='color:red'>这是一封 <b>HTML</b> 邮件</p>", true);
        // 5.添加附件（传入本地文件路径，第二个参数是邮件里显示的文件名）
        helper.addAttachment("demo.txt", new java.io.File("D:\\Desktop\\demo.txt"));
        // 6.真正发送
        mailSender.send(mimeMessage);
    }
}