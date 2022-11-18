package com.dev.school.school.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.school.school.exception.StaffException;
import com.dev.school.school.request.StaffRegRequest;
import com.dev.school.school.response.Response;
import com.dev.school.school.response.StaffRegResponse;
import com.dev.school.school.response.StaffResponse;
import com.dev.school.school.service.StaffService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/staffs")
@Slf4j
public class StaffController {

	private final String USERNAME = "USERNAME";
	private final String SCHOOLID = "SCHOOLID";

	@Autowired
	private StaffService staffService;

	@PostMapping("/")
	public Response<StaffRegResponse> register(@RequestBody StaffRegRequest request,
			@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId) throws StaffException {

		log.info("Request :: register staff to school: {} by admin: {}", schoolId, username);
		StaffRegResponse response = staffService.register(request, username, schoolId);
		log.info("Response :: register staff: {} to school: {} by admin: {}", response, schoolId, username);
		return new Response<StaffRegResponse>(true, null, response);

	}

	@GetMapping("/{id}")
	public Response<StaffResponse> getStaffInfo(@PathVariable String id) throws StaffException {

		log.info("Request: fetch staff info for id: {}", id);
		StaffResponse response = staffService.getStaffInfo(id);
		log.info("Response: fetch staff info for id: {}, response: {}", id, response);
		return new Response<StaffResponse>(true, null, response);

	}

	@GetMapping("/")
	public Response<List<StaffResponse>> getAllStaffsOfSchool(@RequestHeader(SCHOOLID) String schoolId)
			throws StaffException {

		log.info("Request: fetch staffs of school id: {}", schoolId);
		List<StaffResponse> response = staffService.getAllStaffsOfSchool(schoolId);
		log.info("Response: fetch staffs of school id: {}, response: {}", schoolId, response);
		return new Response<List<StaffResponse>>(true, null, response);

	}

	@PutMapping("/{id}")
	public Response<StaffResponse> updateStaffInfo(@RequestBody StaffRegRequest request,
			@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId,
			@PathVariable("id") String staffId) throws StaffException {

		log.info("Request :: update staff to school: {} by admin: {}, staff: {}", schoolId, username, staffId);
		StaffResponse response = staffService.updateStaffInfo(request, username, schoolId, staffId);
		log.info("Response :: update staff: {} to school: {} by admin: {}, staff: {}", response, schoolId, username,
				staffId);
		return new Response<StaffResponse>(true, null, response);

	}
	
	@PatchMapping("/{id}")
	public Response<List<StaffResponse>> deleteStaff(@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId,
			@PathVariable("id") String staffId) throws StaffException {

		log.info("Request :: delete staff to school: {} by admin: {}, staff: {}", schoolId, username, staffId);
		List<StaffResponse> response = staffService.deleteStaff(username, schoolId, staffId);
		log.info("Response :: delete staff: {} to school: {} by admin: {}, staff: {}", response, schoolId, username,
				staffId);
		return new Response<List<StaffResponse>>(true, null, response);

	}

}
