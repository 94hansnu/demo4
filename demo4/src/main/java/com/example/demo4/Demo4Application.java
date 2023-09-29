package com.example.demo4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo4Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo4Application.class, args);

		Main main = new Main();
		try{
			main.main(args);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
