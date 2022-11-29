package com.dev.school.school.request;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.enumeration.SubjectEnum;

import lombok.Data;

@Data
public class ClassRegRequest {
	private StdEnum std;
	private SubjectEnum subject;
	private String staff;
	private long start;
	private long end;
	private boolean isRecurring;
}
