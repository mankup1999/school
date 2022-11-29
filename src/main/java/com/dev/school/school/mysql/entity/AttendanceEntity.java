package com.dev.school.school.mysql.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity(name = "attendance")
@Data
public class AttendanceEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String classId;
	private Date date;
	@OneToMany
	private List<StudentEntity> presents;

}
