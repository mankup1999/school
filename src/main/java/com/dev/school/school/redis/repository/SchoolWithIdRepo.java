package com.dev.school.school.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.redis.entity.SchoolWithId;

@Repository
public interface SchoolWithIdRepo extends CrudRepository<SchoolWithId, String> {

}
