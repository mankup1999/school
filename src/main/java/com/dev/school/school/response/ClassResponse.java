package com.dev.school.school.response;

import java.io.Serializable;
import java.util.Set;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.enumeration.SubjectEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ClassResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String school;
	private StdEnum std;
	private SubjectEnum subject;
	private String staff;
	private long start;
	private long end;
	private boolean isRecurring;
	private Set<StudentResponse> students;
}
