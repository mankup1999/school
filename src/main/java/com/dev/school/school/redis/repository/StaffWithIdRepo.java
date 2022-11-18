package com.dev.school.school.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.redis.entity.StaffWithId;

@Repository
public interface StaffWithIdRepo extends CrudRepository<StaffWithId, String> {

}
