package com.dev.school.school.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.redis.entity.AdminSchools;

@Repository
public interface AdminSchoolsRepo extends CrudRepository<AdminSchools, String> {

}
