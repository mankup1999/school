package com.dev.school.school.redis.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import com.dev.school.school.enumeration.Designation;
import com.dev.school.school.enumeration.IdType;

import lombok.Data;

@RedisHash(value = "StaffWithId", timeToLive = 86400)
@Data
public class StaffWithId implements Serializable {

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
