package com.dd.loader.betterreadsdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.dd.loader.betterreadsdataloader.Entity.Author;
import com.dd.loader.betterreadsdataloader.Repository.AuthorRepository;
import com.dd.loader.betterreadsdataloader.connection.DataStaxAstraProperties;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
	}
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
