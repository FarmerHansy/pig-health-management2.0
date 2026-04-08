package com.linyi.pig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.linyi.pig.mapper")
public class PigHealthSmartMedicineApplication {
    public static void main(String[] args) {
        SpringApplication.run(PigHealthSmartMedicineApplication.class, args);
    }
}
