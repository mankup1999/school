package com.dev.school.school.mysql.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.mysql.entity.ClassEntity;

@Repository
public interface ClassRepo extends CrudRepository<ClassEntity, String> {
	public List<ClassEntity> findAllBySchoolAndStd(String school, StdEnum std);
	
	@Modifying
	@Transactional
	@Query("UPDATE class SET staff = :staff WHERE id = :id")
	void updateStaff(@Param(value = "id") String id, @Param(value = "staff") String staff);
}
