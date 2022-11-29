package com.dev.school.school.mysql.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "class_goal")
@Data
public class ClassGoalsEntity implements Serializable {

	private static final long serialVersionUID = -8365195841768641551L;

	@Id
	private String id;
	private String classId;
	private Date date;
	private String topic;
	private String bulletPoints;

}
