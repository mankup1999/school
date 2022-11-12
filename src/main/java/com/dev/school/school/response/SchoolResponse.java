package com.dev.school.school.response;

import com.dev.school.school.mysql.entity.SchoolEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class SchoolResponse {
	@JsonProperty(value = "school")
	private SchoolEntity schoolEntity;
}
