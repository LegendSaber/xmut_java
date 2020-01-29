package com.xmut.xmut_java;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xumt.xmut_java.*.mapper")
public class XmutJavaApplication {
	public static void main(String[] args) {
		SpringApplication.run(XmutJavaApplication.class, args);
	}
}
