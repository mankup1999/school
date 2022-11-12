package com.dev.school.school.mysql.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.dev.school.school.enumeration.SchoolEnum;

import lombok.Data;

@Entity(name = "school")
@Data
public class SchoolEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String name;
	private SchoolEnum type;
	private String logo;
	private String registrationId;
	private String email;
	private String phone;
	private String admin;

}
