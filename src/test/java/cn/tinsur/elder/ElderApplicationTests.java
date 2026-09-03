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

}