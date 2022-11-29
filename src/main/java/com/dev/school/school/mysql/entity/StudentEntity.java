package com.dev.school.school.mysql.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "students")
@Data
public class StudentEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String school;

}
