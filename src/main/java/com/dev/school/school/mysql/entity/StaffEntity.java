package com.dev.school.school.mysql.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.dev.school.school.enumeration.Designation;
import com.dev.school.school.enumeration.IdType;

import lombok.Data;

@Entity(name = "staff")
@Data
public class StaffEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String name;
	private String photo;
	private Date dob;
	private String email;
	private String phone;
	private IdType idType;
	private String idNo;
	private Designation designation;
	private String school;

}
