package com.dev.school.school.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.mysql.entity.ClassGoalsEntity;

@Repository
public interface ClassGoalsRepo extends CrudRepository<ClassGoalsEntity, String> {
	
}
