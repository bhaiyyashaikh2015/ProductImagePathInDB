package com.ex.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/images")
public class ImageController {

//	private final Path rootLocation = Paths.get("product-images");

	@GetMapping("/bytes/{filename:.+}")
	public ResponseEntity<?> serveFile(@PathVariable String filename) {
		System.out.println("Loading image as bytes");

		Path filePath = Paths.get("product-images").resolve(filename).normalize();
		byte[] imageBytes = null;
		try {
			Resource resource = new UrlResource(filePath.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			imageBytes = Files.readAllBytes(filePath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

	}

	@GetMapping("/file/{filename:.+}")
	public ResponseEntity<Resource> getImage(@PathVariable String filename) {
		System.out.println("Loading image as file");
		try {
			Path filePath = Paths.get("product-images").resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			 MediaType contentType = MediaType.valueOf(Files.probeContentType(filePath));

			if (contentType == null) {
				contentType = MediaType.IMAGE_JPEG;
			}
			
			System.out.println("resource.getFilename() ==> "+resource.getFilename());

			return ResponseEntity.ok().contentType(contentType)
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
					.body(resource);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
