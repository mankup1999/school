package com.dev.school.school.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.school.school.exception.StaffException;
import com.dev.school.school.mysql.entity.StaffEntity;
import com.dev.school.school.mysql.repository.SchoolRepo;
import com.dev.school.school.mysql.repository.StaffRepo;
import com.dev.school.school.redis.entity.SchoolStaffs;
import com.dev.school.school.redis.entity.StaffWithId;
import com.dev.school.school.redis.repository.SchoolStaffsRepo;
import com.dev.school.school.redis.repository.StaffWithIdRepo;
import com.dev.school.school.request.StaffRegRequest;
import com.dev.school.school.response.StaffRegResponse;
import com.dev.school.school.response.StaffResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StaffService {

	@Autowired
	private SchoolRepo schoolRepo;

	@Autowired
	private StaffRepo staffRepo;

	@Autowired
	private SchoolStaffsRepo schoolStaffsRepo;

	@Autowired
	private StaffWithIdRepo staffWithIdRepo;

	public StaffRegResponse register(StaffRegRequest request, String username, String schoolId) throws StaffException {

		log.info("Staff: {} registration started... Admin: {}, School: {}", request, username, schoolId);

		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new StaffException("You can't add staffs");
			}

			StaffEntity staffEntity = getStaffEntity(request, schoolId);
			staffRepo.save(staffEntity);

			invalidateSchoolStaffs(schoolId);

			log.info("Staff: {} registration completed... Admin: {}, School: {}, Staff: {}", request, username,
					schoolId, staffEntity);
			return StaffRegResponse.builder().id(staffEntity.getId()).build();

		} catch (Exception exception) {
			log.info("Staff: {} registration has encountered an error... Admin: {}, School: {}, Error: {}", request,
					username, schoolId, exception.getMessage());
			throw new StaffException(exception.getMessage());
		}

	}

	private void invalidateSchoolStaffs(String schoolId) {
		log.info("Invalidating SchoolStaffs cache for school id:{}", schoolId);
		schoolStaffsRepo.deleteById(schoolId);
	}

	private StaffEntity getStaffEntity(StaffRegRequest request, String school) {
		StaffEntity staffEntity = new StaffEntity();
		staffEntity.setId(generateStaffId());
		staffEntity.setName(request.getName());
		staffEntity.setPhoto(request.getPhoto());
		staffEntity.setDob(request.getDob());
		staffEntity.setEmail(request.getEmail());
		staffEntity.setPhone(request.getPhone());
		staffEntity.setIdType(request.getIdType());
		staffEntity.setIdNo(request.getIdNo());
		staffEntity.setDesignation(request.getDesignation());
		staffEntity.setSchool(school);
		return staffEntity;
	}

	private String generateStaffId() {
		return UUID.randomUUID().toString();
	}

	private boolean checkAdminAndSchoolExists(String username, String schoolId) {
		log.info("Checking whether the user: {} is admin of school: {} or not", username, schoolId);
		Optional<String> optionalId = schoolRepo.findByIdAndUsername(schoolId, username);
		return !optionalId.isEmpty() && optionalId.get() != null;
	}

	public StaffResponse getStaffInfo(String id) throws StaffException {

		log.info("fetching staff info for id: {}", id);
		try {

			return getStaffUsingId(id);

		} catch (Exception exception) {
			log.info("fetching staff info for id: {} has encountered an error: {}", id, exception.getMessage());
			throw new StaffException(exception.getMessage());
		}

	}

	private StaffResponse getStaffUsingId(String id) {
		StaffWithId staffWithId = getStaffFromCache(id);

		if (staffWithId != null) {
			return convertStaffUsingStaffCache(staffWithId);
		}

		StaffEntity staffEntity = getStaffFromDb(id);
		staffWithId = convertStaffEntityToCache(staffEntity);
		addToStaffCache(staffWithId);
		return convertStaffUsingDb(staffEntity);
	}

	private void addToStaffCache(StaffWithId staffWithId) {

		if (staffWithId == null) {
			return;
		}

		log.info("Adding to staffWithId cache id: {}", staffWithId.getId());
		staffWithIdRepo.save(staffWithId);

	}

	private StaffWithId convertStaffEntityToCache(StaffEntity staffEntity) {

		if (staffEntity == null) {
			return null;
		}

		StaffWithId staffWithId = new StaffWithId();
		staffWithId.setId(staffEntity.getId());
		staffWithId.setName(staffEntity.getName());
		staffWithId.setPhoto(staffEntity.getPhoto());
		staffWithId.setDob(staffEntity.getDob());
		staffWithId.setEmail(staffEntity.getEmail());
		staffWithId.setPhone(staffEntity.getPhone());
		staffWithId.setIdType(staffEntity.getIdType());
		staffWithId.setIdNo(staffEntity.getIdNo());
		staffWithId.setDesignation(staffEntity.getDesignation());
		staffWithId.setSchool(staffEntity.getSchool());
		return staffWithId;
	}

	private StaffEntity getStaffFromDb(String id) {

		log.info("Searching in staffWithId db for id: {}", id);

		Optional<StaffEntity> optionalStaffEntity = staffRepo.findById(id);
		StaffEntity staffEntity = null;

		if (optionalStaffEntity.isEmpty()) {
			return staffEntity;
		}

		staffEntity = optionalStaffEntity.get();

		return staffEntity;
	}

	private StaffResponse convertStaffUsingDb(StaffEntity staffEntity) {

		if (staffEntity == null) {
			return null;
		}

		return StaffResponse.builder().id(staffEntity.getId()).name(staffEntity.getName()).photo(staffEntity.getPhoto())
				.dob(staffEntity.getDob()).email(staffEntity.getEmail()).phone(staffEntity.getPhone())
				.designation(staffEntity.getDesignation()).school(staffEntity.getSchool()).build();
	}

	private StaffWithId getStaffFromCache(String id) {

		log.info("Searching in staffWithId cache for id: {}", id);

		Optional<StaffWithId> optionalStaffWithId = staffWithIdRepo.findById(id);
		StaffWithId staffWithId = null;

		if (optionalStaffWithId.isEmpty()) {
			return staffWithId;
		}

		staffWithId = optionalStaffWithId.get();

		return staffWithId;
	}

	private StaffResponse convertStaffUsingStaffCache(StaffWithId staffWithId) {

		if (staffWithId == null) {
			return null;
		}

		return StaffResponse.builder().id(staffWithId.getId()).name(staffWithId.getName()).photo(staffWithId.getPhoto())
				.dob(staffWithId.getDob()).email(staffWithId.getEmail()).phone(staffWithId.getPhone())
				.designation(staffWithId.getDesignation()).school(staffWithId.getSchool()).build();
	}

	public List<StaffResponse> getAllStaffsOfSchool(String schoolId) throws StaffException {
		log.info("Fetching staffs from school: {}", schoolId);
		try {

			List<String> staffIds = getSchoolStaffsUsingFromCache(schoolId);
			List<StaffResponse> response = getStaffUsingIdList(staffIds);
			if (!response.isEmpty()) {
				return response;
			}

			List<StaffEntity> staffEntities = getSchoolStaffsUsingFromDb(schoolId);
			addToStaffSchoolsCache(schoolId, staffEntities);
			response = populateStaffsIntoResponse(staffEntities);
			return response;

		} catch (Exception exception) {
			log.info("Fetching staffs from school: {} has encountered an error: {}", schoolId, exception.getMessage());
			throw new StaffException(exception.getMessage());
		}
	}

	private void addToStaffSchoolsCache(String schoolId, List<StaffEntity> staffEntities) {

		log.info("Adding to SchoolsStaffCache school id: {}", schoolId);

		if (staffEntities.isEmpty()) {
			return;
		}

		List<String> ids = new ArrayList<String>();

		for (StaffEntity staffEntity : staffEntities) {
			String id = staffEntity.getId();
			ids.add(id);
		}

		SchoolStaffs schoolStaffs = new SchoolStaffs();
		schoolStaffs.setId(schoolId);
		schoolStaffs.setStaffs(ids);

		schoolStaffsRepo.save(schoolStaffs);

	}

	private List<StaffResponse> getStaffUsingIdList(List<String> staffIds) {
		List<StaffResponse> staffResponses = new ArrayList<StaffResponse>();
		if (staffIds == null || staffIds.isEmpty()) {
			return staffResponses;
		}
		for (String id : staffIds) {
			staffResponses.add(getStaffUsingId(id));
		}
		return staffResponses;
	}

	private List<String> getSchoolStaffsUsingFromCache(String schoolId) {
		log.info("Fetching staffs of school: {} from cache", schoolId);
		Optional<SchoolStaffs> optionalSchoolStaffs = schoolStaffsRepo.findById(schoolId);

		if (optionalSchoolStaffs.isEmpty()) {
			return null;
		}

		return optionalSchoolStaffs.get().getStaffs();
	}

	private List<StaffEntity> getSchoolStaffsUsingFromDb(String schoolId) {
		log.info("Fetching staffs of school: {} from db", schoolId);
		List<StaffEntity> staffEntities = staffRepo.findBySchool(schoolId);
		return staffEntities;
	}

	private List<StaffResponse> populateStaffsIntoResponse(List<StaffEntity> staffEntities) {
		List<StaffResponse> response = new ArrayList<StaffResponse>();
		if (staffEntities.isEmpty()) {
			return response;
		}

		for (StaffEntity staffEntity : staffEntities) {
			response.add(convertStaffUsingDb(staffEntity));
		}
		return response;
	}

	public StaffResponse updateStaffInfo(StaffRegRequest request, String username, String schoolId, String staffId)
			throws StaffException {
		log.info("Updating staff: {} with info:{} by user: {}", staffId, request, username);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new StaffException("You can't update staffs");
			}

			updateStaffInDb(staffId, request, schoolId);
			updateStaffInCache(staffId, request, schoolId);

			StaffWithId staffWithId = getStaffFromCache(staffId);

			StaffResponse response = convertStaffUsingStaffCache(staffWithId);

			return response;

		} catch (Exception exception) {
			log.info("Updating staff: {} with info:{} by user: {} has encountered an error: {}", staffId, request,
					username, exception.getMessage());
			throw new StaffException(exception.getMessage());
		}
	}

	private void updateStaffInCache(String staffId, StaffRegRequest request, String schoolId) {
		log.info("Updating staff: {} into cache", staffId);
		StaffWithId staffWithId = new StaffWithId();
		staffWithId.setId(staffId);
		staffWithId.setName(request.getName());
		staffWithId.setPhoto(request.getPhoto());
		staffWithId.setDob(request.getDob());
		staffWithId.setEmail(request.getEmail());
		staffWithId.setPhone(request.getPhone());
		staffWithId.setIdType(request.getIdType());
		staffWithId.setIdNo(request.getIdNo());
		staffWithId.setDesignation(request.getDesignation());
		staffWithId.setSchool(schoolId);

		staffWithIdRepo.save(staffWithId);
	}

	private void updateStaffInDb(String staffId, StaffRegRequest request, String schoolId) {
		log.info("Updating staff: {} into db", staffId);
		StaffEntity staffEntity = new StaffEntity();
		staffEntity.setId(staffId);
		staffEntity.setName(request.getName());
		staffEntity.setPhoto(request.getPhoto());
		staffEntity.setDob(request.getDob());
		staffEntity.setEmail(request.getEmail());
		staffEntity.setPhone(request.getPhone());
		staffEntity.setIdType(request.getIdType());
		staffEntity.setIdNo(request.getIdNo());
		staffEntity.setDesignation(request.getDesignation());
		staffEntity.setSchool(schoolId);

		staffRepo.save(staffEntity);

	}

	public List<StaffResponse> deleteStaff(String username, String schoolId, String staffId) throws StaffException {
		log.info("Deleting staff: {} by user: {}", staffId, username);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new StaffException("You can't delete staffs");
			}

			deleteStaffFromDb(staffId);
			invalidateSchoolStaffs(schoolId);

			List<StaffResponse> response = getAllStaffsOfSchool(schoolId);

			return response;

		} catch (Exception exception) {
			log.info("Deleting staff: {}  by user: {} has encountered an error: {}", staffId, username,
					exception.getMessage());
			throw new StaffException(exception.getMessage());
		}
	}

	private void deleteStaffFromDb(String staffId) {
		log.info("Deleting staff: {} from db", staffId);
		staffRepo.deleteById(staffId);

	}

}
