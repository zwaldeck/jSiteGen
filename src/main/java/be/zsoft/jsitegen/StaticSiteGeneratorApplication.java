package be.zsoft.jsitegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan(basePackages = {"be.zsoft.ssg.command"})
public class StaticSiteGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StaticSiteGeneratorApplication.class, args);
    }

}
