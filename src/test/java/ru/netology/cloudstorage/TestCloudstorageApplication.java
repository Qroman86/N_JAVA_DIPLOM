package ru.netology.cloudstorage;

import org.springframework.boot.SpringApplication;

public class TestCloudstorageApplication {

	public static void main(String[] args) {
		SpringApplication.from(CloudStorageApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
