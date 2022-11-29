package com.dev.school.school.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.mysql.entity.AssignmentEntity;

@Repository
public interface AssignmentRepo extends CrudRepository<AssignmentEntity, String> {
	
}
