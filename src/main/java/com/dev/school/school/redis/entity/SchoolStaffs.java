package com.dev.school.school.redis.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash(value = "SchoolStaffs", timeToLive = 86400)
@Data
public class SchoolStaffs implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private List<String> staffs;

}
