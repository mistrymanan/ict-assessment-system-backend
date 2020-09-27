package com.cdad.project.executionservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication()
public class ExecutionServiceApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	public static void main(String[] args) throws IOException, InterruptedException {

		SpringApplication.run(ExecutionServiceApplication.class, args);
//		Program program=new Program();
//		program.setSourceCode("import java.util.Scanner;\n" +
//				"class Solution {\n" +
//				"\n" +
//				"    public static void main(String[] args) {\n" +
//				"        int num1, num2, sum;\n" +
//				"        Scanner sc = new Scanner(System.in);\n" +
//				"        num1 = sc.nextInt();\n" +
//				"        num2 = sc.nextInt();\n" +
//				"        sc.close();\n" +
//				"        sum = num1 + num2;\n" +
//				"        System.out.println(\"Sum of these numbers: \"+sum);\n" +
//				"    }\n" +
//				"}\n");
//		program.setInput("1\n" +
//				"2");
//		program.setLanguage(Language.JAVA);
//		JavaExecutor javaExecutor=new JavaExecutor(program);
		//javaExecutor.run();
	}
}
