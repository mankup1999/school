package com.dev.school.school.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class StaffException extends Throwable {
	private static final long serialVersionUID = 1L;
	private String message;
}
