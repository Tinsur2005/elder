package cn.tinsur.elder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.tinsur.elder.mapper")
public class ElderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderApplication.class, args);
    }

}
