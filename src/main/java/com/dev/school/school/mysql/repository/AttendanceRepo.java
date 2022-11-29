package com.dev.school.school.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.mysql.entity.AttendanceEntity;

@Repository
public interface AttendanceRepo extends CrudRepository<AttendanceEntity, String> {
	
}
