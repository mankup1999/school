package com.dev.school.school.mysql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dev.school.school.mysql.entity.SchoolEntity;

@Repository
public interface SchoolRepo extends CrudRepository<SchoolEntity, String> {
	@Query(value = "SELECT id FROM DB.school WHERE admin=?1", nativeQuery = true)
	public List<String> findByUsername(String username);

	@Query(value = "SELECT * FROM DB.school WHERE id=?1 LIMIT 1", nativeQuery = true)
	public Optional<SchoolEntity> findById(String id);
	
	@Query(value = "SELECT id FROM DB.school WHERE name LIKE ?1 LIMIT ?2", nativeQuery = true)
	public List<String> findByNameSubstr(String name, int limit);

	@Query(value = "SELECT id FROM DB.school WHERE id=?1 AND admin=?2 LIMIT 1", nativeQuery = true)
	public Optional<String> findByIdAndUsername(String id, String username);
}
