package com.dev.school.school.request;

import com.dev.school.school.enumeration.SchoolEnum;

import lombok.Data;

@Data
public class SchoolRegRequest {
	private String name;
	private SchoolEnum type;
	private String logo;
	private String registrationId;
	private String email;
	private String phone;
}
