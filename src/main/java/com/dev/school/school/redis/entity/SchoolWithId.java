package com.dev.school.school.redis.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.dev.school.school.enumeration.SchoolEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@RedisHash(value = "SchoolWithId", timeToLive = 86400)
@Data
public class SchoolWithId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String name;
	private SchoolEnum type;
	private String logo;
	@JsonIgnore
	private String registrationId;
	@JsonIgnore
	private String email;
	@JsonIgnore
	private String phone;
	@JsonIgnore
	private String admin;

}
