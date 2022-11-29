package com.dev.school.school.mysql.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.enumeration.SubjectEnum;

import lombok.Data;

@Entity(name = "class")
@Data
public class ClassEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String school;
	private StdEnum std;
	private SubjectEnum subject;
	private String staff;
	private long start;
	private long end;
	private boolean isRecurring;
	@OneToMany
	private Set<StudentEntity> students;

}
