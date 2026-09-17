package fdn.fdncargallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FdnCarGalleryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FdnCarGalleryApplication.class, args);
	}

}
