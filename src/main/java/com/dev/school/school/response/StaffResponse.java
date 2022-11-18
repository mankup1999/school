package com.dev.school.school.response;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.dev.school.school.enumeration.Designation;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class StaffResponse implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String name;
	private String photo;
	private Date dob;
	private String email;
	private String phone;
	private Designation designation;
	private String school;

}
