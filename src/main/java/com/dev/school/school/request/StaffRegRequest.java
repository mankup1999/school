package com.dev.school.school.request;

import java.util.Date;

import com.dev.school.school.enumeration.Designation;
import com.dev.school.school.enumeration.IdType;

import lombok.Data;

@Data
public class StaffRegRequest {
	private String name;
	private String photo;
	private Date dob;
	private String email;
	private String phone;
	private IdType idType;
	private String idNo;
	private Designation designation;
}
