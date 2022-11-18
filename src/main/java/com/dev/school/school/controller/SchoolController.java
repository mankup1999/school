package com.dev.school.school.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.redis.entity.SchoolWithId;
import com.dev.school.school.request.SchoolRegRequest;
import com.dev.school.school.response.Response;
import com.dev.school.school.response.SchoolRegResponse;
import com.dev.school.school.response.SchoolResponse;
import com.dev.school.school.service.SchoolService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/schools")
@Slf4j
public class SchoolController {

	private final String USERNAME = "USERNAME";

	@Autowired
	private SchoolService schoolService;

	@PostMapping(path = "/")
	public Response<SchoolRegResponse> register(@RequestBody SchoolRegRequest request,
			@RequestHeader(USERNAME) String username) throws SchoolException {

		log.info("Request :: school registration from username: {} with school info: {}", username, request.toString());

		SchoolRegResponse response = schoolService.register(username, request);

		log.info("Response :: school registration from username: {} with response: {}", username, response.toString());

		return new Response<SchoolRegResponse>(true, null, response);

	}

	@GetMapping(path = "/")
	public Response<List<SchoolWithId>> listSchools(@RequestHeader(USERNAME) String username) throws SchoolException {

		log.info("Request :: school listing for username: {}", username);

		List<SchoolWithId> response = schoolService.listSchools(username);

		log.info("Response :: school listing for username: {} with response: {}", username, response.toString());

		return new Response<List<SchoolWithId>>(true, null, response);

	}

	@GetMapping(path = "/query")
	public Response<List<SchoolWithId>> querySchools(@RequestParam String name,
			@RequestParam(defaultValue = "1") String page,
			@RequestParam(defaultValue = "10") String pageSize
			) throws SchoolException {
		log.info("Request :: querying schools with name: {}", name);

		List<SchoolWithId> response = schoolService.querySchools(name, page, pageSize);

		log.info("Response :: querying schools with name: {} and response: {}", name, response);

		return new Response<List<SchoolWithId>>(true, null, response);
	}

	@PutMapping(path = "/{id}")
	public Response<SchoolWithId> updateSchoolInfo(@PathVariable String id, @RequestBody SchoolRegRequest request,
			@RequestHeader(USERNAME) String username) throws SchoolException {
		log.info("Request :: update schoolinfo with id: {}, user: {}, schoolInfo:{}", id, username, request.toString());

		SchoolWithId response = schoolService.updateSchoolInfo(id, request, username);

		log.info("Response :: update schoolinfo with id: {}, user: {}, schoolInfo:{}", id, username,
				request.toString());

		return new Response<SchoolWithId>(true, null, response);
	}

	@DeleteMapping(path = "/{id}")
	public Response<List<SchoolWithId>> deRegister(@PathVariable String id, @RequestHeader(USERNAME) String username)
			throws SchoolException {

		log.info("Request :: deregistering school id: {} for username: {}", id, username);

		List<SchoolWithId> response = schoolService.deRegister(id, username);

		log.info("Response :: deregistering school id: {} for username: {}", id, username);

		return new Response<List<SchoolWithId>>(true, null, response);

	}

	@GetMapping(path = "/{id}")
	public Response<SchoolResponse> getSchoolInfo(@PathVariable String id,
			@Nullable @RequestHeader(USERNAME) String username) throws SchoolException {

		log.info("Request :: school info for username: {}, id: {}", username, id);

		SchoolResponse response = schoolService.getSchoolInfo(id, username);

		log.info("Response :: school info for username: {}, id: {}, info:{}", username, id, response);

		return new Response<SchoolResponse>(true, null, response);

	}

}
