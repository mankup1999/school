package com.dev.school.school.mysql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.mysql.entity.StaffEntity;

@Repository
public interface StaffRepo extends CrudRepository<StaffEntity, String> {
	
	public List<StaffEntity> findBySchool(String school);
	public Optional<StaffEntity> findByIdAndSchool(String id, String school);
	
}
