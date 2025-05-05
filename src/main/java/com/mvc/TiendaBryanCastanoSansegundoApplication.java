package com.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // importante para el hilo que comprueba los stocks y tareas de spring en
					// general
public class TiendaBryanCastanoSansegundoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaBryanCastanoSansegundoApplication.class, args);
	}

}
