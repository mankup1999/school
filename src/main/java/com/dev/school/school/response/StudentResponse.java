package com.dev.school.school.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class StudentResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
}
