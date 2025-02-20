package com.vlad.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		System.out.println("Hellow");
		SpringApplication.run(TodoApplication.class, args);
	}

}
