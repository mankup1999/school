package com.dev.school.school.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.mysql.entity.SchoolEntity;
import com.dev.school.school.mysql.repository.SchoolRepo;
import com.dev.school.school.redis.entity.AdminSchools;
import com.dev.school.school.redis.entity.SchoolWithId;
import com.dev.school.school.redis.repository.AdminSchoolsRepo;
import com.dev.school.school.redis.repository.SchoolWithIdRepo;
import com.dev.school.school.request.SchoolRegRequest;
import com.dev.school.school.response.SchoolRegResponse;
import com.dev.school.school.response.SchoolResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchoolService {

	@Autowired
	private SchoolRepo schoolRepo;

	@Autowired
	private AdminSchoolsRepo adminSchoolsRepo;

	@Autowired
	private SchoolWithIdRepo schoolWithIdRepo;

	public SchoolRegResponse register(String username, SchoolRegRequest request) throws SchoolException {
		log.info("school register started :: username: {}", username);
		try {

			SchoolEntity entity = getSchoolEntity(username, request);
			String id = entity.getId();

			schoolRepo.save(entity);

			invalidateAdminSchoolsCache(username);

			log.info("school register completed :: username: {} and created school id: {}", username, id);
			return SchoolRegResponse.builder().id(id).build();
		} catch (Exception exception) {
			log.error("school register has encountered an error: {}", exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private void invalidateAdminSchoolsCache(String username) {
		log.info("Invalidating admin school cache");
		adminSchoolsRepo.deleteById(username);
	}

	private SchoolEntity getSchoolEntity(String username, SchoolRegRequest request) {
		String id = UUID.randomUUID().toString();
		SchoolEntity entity = getSchoolEntity(id, username, request);
		return entity;
	}

	private SchoolEntity getSchoolEntity(String id, String username, SchoolRegRequest request) {
		SchoolEntity entity = new SchoolEntity();
		entity.setId(id);
		entity.setName(request.getName());
		entity.setType(request.getType());
		entity.setLogo(request.getLogo());
		entity.setRegistrationId(request.getRegistrationId());
		entity.setEmail(request.getEmail());
		entity.setPhone(request.getPhone());
		entity.setAdmin(username);
		return entity;
	}

	public List<SchoolWithId> listSchools(String username) throws SchoolException {
		log.info("School listing started :: username: {}", username);
		try {

			List<SchoolWithId> schoolWithIds = new ArrayList<SchoolWithId>();

			log.info("Fetching ids from cache for user: {}", username);
			List<String> ids = getAdminSchoolsFromCache(username);
			if (ids.isEmpty()) {
				log.info("Fetching ids from db for user: {}", username);
				ids = schoolRepo.findByUsername(username);
				addToAdminSchoolCache(username, ids);
			}

			schoolWithIds = getSchoolListFromIds(username, ids);

			log.info("school listing completed :: username: {} with schools: {}", username, schoolWithIds);

			return schoolWithIds;

		} catch (Exception exception) {
			log.error("School listing has encountered an error: {}", exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}

	}

	private void addToAdminSchoolCache(String username, List<String> ids) {
		log.info("Saving to adminschool cache user: {}, ids: {}", username, ids);
		AdminSchools adminSchools = new AdminSchools();
		adminSchools.setId(username);
		adminSchools.setSchools(ids);
		adminSchoolsRepo.save(adminSchools);
	}

	private List<String> getAdminSchoolsFromCache(String username) {
		Optional<AdminSchools> optionalAdminSchools = adminSchoolsRepo.findById(username);
		List<String> ids = new ArrayList<String>();
		if (!optionalAdminSchools.isEmpty()) {
			AdminSchools adminSchools = optionalAdminSchools.get();
			ids = adminSchools.getSchools();
		}
		return ids;
	}

	private List<SchoolWithId> getSchoolListFromIds(String username, List<String> ids) {

		List<SchoolWithId> schools = new ArrayList<SchoolWithId>();
		for (String id : ids) {
			log.info("Fetching schools from cache for user: {}, id: {}", username, id);
			SchoolWithId schoolWithId = getSchoolWithIdFromCache(id);
			if (schoolWithId == null) {
				log.info("Fetching schools from db for user: {}, id: {}", username, id);
				schoolWithId = getSchoolWithIdFromDb(id);
				addToSchoolWithId(id, schoolWithId);
			}
			schools.add(schoolWithId);
		}
		return schools;

	}

	private void addToSchoolWithId(String id, SchoolWithId schoolWithId) {
		log.info("Saving to schoolwithID cache id: {}", id);
		schoolWithIdRepo.save(schoolWithId);
	}

	private SchoolWithId getSchoolWithIdFromCache(String id) {
		log.info("Fetching school from cache for id: {}", id);
		SchoolWithId schoolWithId = null;
		Optional<SchoolWithId> optionalSchoolWithId = schoolWithIdRepo.findById(id);
		if (!optionalSchoolWithId.isEmpty()) {
			schoolWithId = optionalSchoolWithId.get();
			return schoolWithId;
		}
		return schoolWithId;
	}

	private SchoolWithId getSchoolWithIdFromDb(String id) {
		log.info("Fetching school from db for id: {}", id);
		SchoolWithId schoolWithId = null;
		Optional<SchoolEntity> optionalSchoolEntity = schoolRepo.findById(id);
		if (!optionalSchoolEntity.isEmpty()) {
			SchoolEntity entity = optionalSchoolEntity.get();
			schoolWithId = getSchool(entity);
		}
		return schoolWithId;
	}

	private SchoolWithId getSchool(SchoolEntity schoolEntity) {
		SchoolWithId schoolWithId = new SchoolWithId();
		schoolWithId.setId(schoolEntity.getId());
		schoolWithId.setName(schoolEntity.getName());
		schoolWithId.setType(schoolEntity.getType());
		schoolWithId.setLogo(schoolEntity.getLogo());
		schoolWithId.setAdmin(schoolEntity.getAdmin());
		schoolWithId.setRegistrationId(schoolEntity.getRegistrationId());
		schoolWithId.setEmail(schoolEntity.getEmail());
		schoolWithId.setPhone(schoolEntity.getPhone());
		return schoolWithId;
	}

	public List<SchoolWithId> querySchools(String name, String page, String pageSize)
			throws SchoolException {
		log.info("Querying schools started :: name: {}", name);
		try {

			Map<String, Integer> pageMap = getPageMap(page, pageSize);

			String nameSubstr = name + "%";
			List<String> ids = schoolRepo.findByNameSubstr(nameSubstr, pageMap.get("limit"), pageMap.get("offset"));
			return getSchoolListFromIds(ids);
		} catch (Exception exception) {
			log.error("querying schools has encountered an error: {}", exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private Map<String, Integer> getPageMap(String page, String pageSize) {
		Map<String, Integer> pageMap = new HashMap<String, Integer>();

		int offset = Integer.parseInt(page);
		int limit = Integer.parseInt(pageSize);

		offset = (offset - 1) * limit;

		pageMap.put("offset", offset);
		pageMap.put("limit", limit);

		return pageMap;
	}

	private List<SchoolWithId> getSchoolListFromIds(List<String> ids) {

		List<SchoolWithId> schools = new ArrayList<SchoolWithId>();
		for (String id : ids) {
			log.info("Fetching schools from cache for id: {}", id);
			SchoolWithId schoolWithId = getSchoolFromId(id);
			schools.add(schoolWithId);
		}
		return schools;

	}

	private SchoolWithId getSchoolFromId(String id) {
		log.info("Fetching schools from cache for id: {}", id);
		SchoolWithId schoolWithId = getSchoolWithIdFromCache(id);
		if (schoolWithId == null) {
			log.info("Fetching schools from db for id: {}", id);
			schoolWithId = getSchoolWithIdFromDb(id);
		}
		return schoolWithId;
	}

	public SchoolWithId updateSchoolInfo(String id, SchoolRegRequest request, String username) throws SchoolException {
		log.info("Starting to update school for id: {}, user: {}", id, username);
		try {

			boolean exists = checkExistWithIdAndUsername(id, username);
			if (!exists) {
				throw new SchoolException("You can't update it");
			}

			SchoolWithId latestSchoolWithId = getSchoolWithId(id, username, request);
			updateSchoolInCache(id, latestSchoolWithId);

			SchoolEntity latestSchoolEntity = getSchoolEntity(id, username, request);
			updateSchoolInDb(id, latestSchoolEntity);

			log.info("Completed updating school for id: {}, user: {}", id, username);

			return latestSchoolWithId;
		} catch (Exception exception) {
			log.error("Updating school id: {} has encountered an error: {}", id, exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private boolean checkExistWithIdAndUsername(String id, String username) {
		Optional<String> optionalId = schoolRepo.findByIdAndUsername(id, username);
		return !optionalId.isEmpty() && optionalId.get() != null;
	}

	private SchoolWithId getSchoolWithId(String id, String admin, SchoolRegRequest request) {
		SchoolWithId schoolWithId = new SchoolWithId();
		schoolWithId.setId(id);
		schoolWithId.setName(request.getName());
		schoolWithId.setType(request.getType());
		schoolWithId.setLogo(request.getLogo());
		schoolWithId.setAdmin(admin);
		schoolWithId.setRegistrationId(request.getRegistrationId());
		schoolWithId.setEmail(request.getEmail());
		schoolWithId.setPhone(request.getPhone());
		return schoolWithId;
	}

	public void updateSchoolInCache(String id, SchoolWithId schoolWithId) {
		log.info("Updating school cache for id: {}, school: {}", id, schoolWithId.toString());
		schoolWithIdRepo.save(schoolWithId);
	}

	public void updateSchoolInDb(String id, SchoolEntity schoolEntity) {
		log.info("Updating school db for id: {}, school: {}", id, schoolEntity.toString());
		schoolRepo.save(schoolEntity);
	}

	public List<SchoolWithId> deRegister(String id, String username) throws SchoolException {
		log.info("Starting to deregister school for id: {}, user: {}", id, username);
		try {

			boolean exists = checkExistWithIdAndUsername(id, username);
			if (!exists) {
				throw new SchoolException("You can't deregister it");
			}

			deleteSchoolWithIdFromCache(id);
			invalidateAdminSchoolsCache(username);
			deRegisterSchoolFromDB(id);

			List<SchoolWithId> schoolListResponse = listSchools(username);

			log.info("Completed deregister school for id: {}, user: {}", id, username);

			return schoolListResponse;
		} catch (Exception exception) {
			log.error("DeRegistering school id: {} and user:{} has encountered an error: {}", id, username,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	public void deleteSchoolWithIdFromCache(String id) {
		log.info("Deleting from school cache id: {}", id);
		schoolWithIdRepo.deleteById(id);
	}

	public void deRegisterSchoolFromDB(String id) {
		log.info("Deleting from school db id: {}", id);
		schoolRepo.deleteById(id);
	}

	public SchoolResponse getSchoolInfo(String id, String username) throws SchoolException {
		log.info("Starting to fetch school for id: {}, user: {}", id, username);
		try {

			SchoolWithId schoolWithId = getSchoolWithIdFromCache(id);
			if (schoolWithId == null) {
				schoolWithId = getSchoolWithIdFromDb(id);
				updateSchoolInCache(id, schoolWithId);
			}

			SchoolEntity schoolEntity = getSchoolEntityFromSchoolWithId(schoolWithId);

			SchoolResponse schoolResponse = SchoolResponse.builder().schoolEntity(schoolEntity).build();

			log.info("Completed fetching school for id: {}, user: {}", id, username);

			return schoolResponse;
		} catch (Exception exception) {
			log.error("Fetching school id: {} and user:{} has encountered an error: {}", id, username,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private SchoolEntity getSchoolEntityFromSchoolWithId(SchoolWithId schoolWithId) {
		SchoolEntity schoolEntity = new SchoolEntity();
		schoolEntity.setId(schoolWithId.getId());
		schoolEntity.setName(schoolWithId.getName());
		schoolEntity.setType(schoolWithId.getType());
		schoolEntity.setLogo(schoolWithId.getLogo());
		schoolEntity.setAdmin(schoolWithId.getAdmin());
		schoolEntity.setRegistrationId(schoolWithId.getRegistrationId());
		schoolEntity.setEmail(schoolWithId.getEmail());
		schoolEntity.setPhone(schoolWithId.getPhone());
		return schoolEntity;
	}

}
