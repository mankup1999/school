package com.dev.school.school.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.request.AssignStaffToClassRequest;
import com.dev.school.school.request.ClassRegRequest;
import com.dev.school.school.request.EnrollStudentToClassRequest;
import com.dev.school.school.response.ClassRegResponse;
import com.dev.school.school.response.ClassResponse;
import com.dev.school.school.response.Response;
import com.dev.school.school.service.ClassService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/classes")
@Slf4j
public class ClassController {

	private final String USERNAME = "USERNAME";
	private final String SCHOOLID = "SCHOOLID";

	@Autowired
	private ClassService classService;

	@PostMapping("/")
	public Response<ClassRegResponse> register(@RequestBody ClassRegRequest request,
			@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: register class to school: {} by admin: {}", schoolId, username);
		ClassRegResponse response = classService.register(request, username, schoolId);
		log.info("Response :: register class: {} to school: {} by admin: {}", response, schoolId, username);
		return new Response<ClassRegResponse>(true, null, response);

	}

	@GetMapping("/{id}")
	public Response<ClassResponse> getClassById(@PathVariable String id) throws SchoolException {

		log.info("Request :: get class for id: {}", id);
		ClassResponse response = classService.getClassById(id);
		log.info("Responset :: get class for id: {} -> {}", id, response);
		return new Response<ClassResponse>(true, null, response);

	}

	@GetMapping("/std/{std}")
	public Response<List<ClassResponse>> getClassListByStd(@PathVariable StdEnum std,
			@RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: get classes for std: {}", std);
		List<ClassResponse> response = classService.getClassListByStd(std, schoolId);
		log.info("Responset :: getclasses for std: {} -> {}", std, response);
		return new Response<List<ClassResponse>>(true, null, response);

	}

	@PatchMapping("/{id}/assignStaff")
	public Response<ClassResponse> assignStaff(@PathVariable String id, @RequestBody AssignStaffToClassRequest request,
			@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: assign staff: {} to class: {} for school: {} by user: {}", request.getStaff(), id,
				schoolId, username);
		ClassResponse response = classService.assignStaff(id, request.getStaff(), username, schoolId);
		log.info("Response :: assign staff: {} to class: {} for school: {} by user: {} :: response: {}",
				request.getStaff(), id, schoolId, username, response);
		return new Response<ClassResponse>(true, null, response);

	}

	@PatchMapping("/{id}/enrollStudent")
	public Response<ClassResponse> enrollStudent(@PathVariable String id,
			@RequestBody EnrollStudentToClassRequest request, @RequestHeader(USERNAME) String username,
			@RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: enroll student: {} to class: {} for school: {} by user: {}", request.getStudent(), id,
				schoolId, username);
		ClassResponse response = classService.enrollStudent(id, request.getStudent(), username, schoolId);
		log.info("Response :: enroll student: {} to class: {} for school: {} by user: {} :: response: {}",
				request.getStudent(), id, schoolId, username, response);
		return new Response<ClassResponse>(true, null, response);

	}

	@PutMapping("/{id}")
	public Response<ClassResponse> updateClassInfo(@PathVariable String id, @RequestBody ClassRegRequest request,
			@RequestHeader(USERNAME) String username, @RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: update class: {} with the requested info: {}", id, request);
		ClassResponse response = classService.updateClassInfo(id, request, username, schoolId);
		log.info("Response :: update class: {} with the requested info: {} --> {}", id, request, response);
		return new Response<ClassResponse>(true, null, response);

	}

	@PatchMapping("/{id}/renew")
	public Response<ClassResponse> renew(@PathVariable String id, @RequestHeader(USERNAME) String username,
			@RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: renew class: {}", id);
		ClassResponse response= classService.renew(id, username, schoolId);
		log.info("Response :: renew class: {} --> {}", id, response);
		return new Response<ClassResponse>(true, null, response);

	}

	@DeleteMapping("/{id}")
	public Response<Boolean> deleteClass(@PathVariable String id, @RequestHeader(USERNAME) String username,
			@RequestHeader(SCHOOLID) String schoolId) throws SchoolException {

		log.info("Request :: delete class: {}", id);
		classService.deleteClass(id, username, schoolId);
		log.info("Response :: delete class: {}", id);
		return new Response<Boolean>(true, null, true);

	}

}
