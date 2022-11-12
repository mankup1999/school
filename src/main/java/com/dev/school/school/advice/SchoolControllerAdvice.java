package com.dev.school.school.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.response.Error;
import com.dev.school.school.response.Response;
import com.dev.school.school.response.SchoolRegResponse;

@RestControllerAdvice
public class SchoolControllerAdvice {

	@ExceptionHandler(SchoolException.class)
	public Response<SchoolRegResponse> schoolException(SchoolException exception) {
		Error error = new Error(exception.getMessage());
		return new Response<SchoolRegResponse>(false, error, null);
	}

}
