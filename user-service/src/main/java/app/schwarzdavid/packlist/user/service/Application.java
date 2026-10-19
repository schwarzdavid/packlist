package app.schwarzdavid.packlist.user.service;

import app.schwarzdavid.packlist.common.annotation.EnablePacklistCommon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePacklistCommon
public class Application {
    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
