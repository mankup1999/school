package com.dev.school.school.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = { "/", "/health" })
@Slf4j
public class PingController {

	@GetMapping
	public String ping() {
		log.info("Request :: ping to server");
		String health = "OK";
		return health;
	}

}
