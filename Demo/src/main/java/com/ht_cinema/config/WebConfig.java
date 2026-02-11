package com.ht_cinema.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path filmImagesDir = Paths.get(System.getProperty("user.dir"), "film-images");
		registry.addResourceHandler("/film-images/**")
				.addResourceLocations("file:" + filmImagesDir.toAbsolutePath().toString() + "/");

		Path postersDir = Paths.get(System.getProperty("user.dir"), "poster-images");
		registry.addResourceHandler("/poster-images/**")
				.addResourceLocations("file:" + postersDir.toAbsolutePath().toString() + "/");
		   Path productImagesDir = Paths.get(System.getProperty("user.dir"), "product-images");
		    registry.addResourceHandler("/product-images/**")
		            .addResourceLocations("file:" + productImagesDir.toAbsolutePath().toString() + "/");
		    Path uploadsDir = Paths.get(System.getProperty("user.dir"), "uploads");
		    registry.addResourceHandler("/uploads/**")
		            .addResourceLocations("file:" + uploadsDir.toAbsolutePath().toString() + "/");
	}
}
