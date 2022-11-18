package com.dev.school.school.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.exception.StaffException;
import com.dev.school.school.response.Error;
import com.dev.school.school.response.Response;

@RestControllerAdvice
public class SchoolControllerAdvice {

	@ExceptionHandler(SchoolException.class)
	public Response<Object> schoolException(SchoolException exception) {
		Error error = new Error(exception.getMessage());
		return new Response<Object>(false, error, null);
	}

	@ExceptionHandler(StaffException.class)
	public Response<Object> staffException(StaffException exception) {
		Error error = new Error(exception.getMessage());
		return new Response<Object>(false, error, null);
	}

}
