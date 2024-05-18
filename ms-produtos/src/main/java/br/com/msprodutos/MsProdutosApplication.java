package br.com.msprodutos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsProdutosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProdutosApplication.class, args);
	}

}
