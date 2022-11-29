package com.dev.school.school.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.school.school.enumeration.StdEnum;
import com.dev.school.school.exception.SchoolException;
import com.dev.school.school.mysql.entity.ClassEntity;
import com.dev.school.school.mysql.entity.StaffEntity;
import com.dev.school.school.mysql.entity.StudentEntity;
import com.dev.school.school.mysql.repository.ClassRepo;
import com.dev.school.school.mysql.repository.SchoolRepo;
import com.dev.school.school.mysql.repository.StaffRepo;
import com.dev.school.school.mysql.repository.StudentRepo;
import com.dev.school.school.request.ClassRegRequest;
import com.dev.school.school.response.ClassRegResponse;
import com.dev.school.school.response.ClassResponse;
import com.dev.school.school.response.StudentResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassService {

	@Autowired
	private ClassRepo classRepo;

	@Autowired
	private SchoolRepo schoolRepo;

	@Autowired
	private StaffRepo staffRepo;

	@Autowired
	private StudentRepo studentRepo;

	public ClassRegResponse register(ClassRegRequest request, String username, String schoolId) throws SchoolException {
		log.info("Adding class: {} to school: {} started...", request, schoolId);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You can't register a class");
			}

			if (!checkStaffExists(schoolId, request.getStaff())) {
				throw new SchoolException("Your staff doesn't exist");
			}

			ClassEntity classEntity = new ClassEntity();
			String id = getUUID();
			classEntity.setId(id);
			classEntity.setSchool(schoolId);
			classEntity.setStd(request.getStd());
			classEntity.setSubject(request.getSubject());
			classEntity.setStaff(request.getStaff());
			classEntity.setStart(request.getStart());
			classEntity.setEnd(request.getEnd());
			classEntity.setRecurring(request.isRecurring());
			Set<StudentEntity> students = new HashSet<StudentEntity>();
			classEntity.setStudents(students);

			classRepo.save(classEntity);

			log.info("Adding class: {} to school: {} completed!!!", id, schoolId);

			return ClassRegResponse.builder().id(id).build();

		} catch (Exception exception) {
			log.error("Adding class: {} to school: {} has encountered an error: {}", request, schoolId,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private String getUUID() {
		return UUID.randomUUID().toString();
	}

	private boolean checkAdminAndSchoolExists(String username, String schoolId) {
		log.info("Checking whether the user: {} is admin of school: {} or not", username, schoolId);
		Optional<String> optionalId = schoolRepo.findByIdAndUsername(schoolId, username);
		return !optionalId.isEmpty() && optionalId.get() != null;
	}

	private boolean checkStaffExists(String schoolId, String staff) {
		Optional<StaffEntity> optionalStaff = staffRepo.findByIdAndSchool(staff, schoolId);
		return !optionalStaff.isEmpty() && optionalStaff.get() != null;
	}

	public ClassResponse getClassById(String id) throws SchoolException {
		log.info("Fetching class info for id: {} started...", id);
		try {

			ClassEntity classEntity = getClassFromDB(id);

			if (classEntity == null) {
				log.info("No class found for id: {}", id);
				return null;
			}

			ClassResponse classResponse = convertClassEntityToResponse(classEntity);

			log.info("Fetching class info for id: {} completed!!!", id);
			return classResponse;
		} catch (Exception exception) {
			log.error("Fetching class info for id: {} has encountered an error: {}", id, exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private ClassResponse convertClassEntityToResponse(ClassEntity classEntity) {

		if (classEntity == null) {
			return ClassResponse.builder().build();
		}

		ClassResponse classResponse = ClassResponse.builder().id(classEntity.getId()).school(classEntity.getSchool())
				.std(classEntity.getStd()).staff(classEntity.getStaff()).start(classEntity.getStart())
				.end(classEntity.getEnd()).isRecurring(classEntity.isRecurring())
				.students(populateStudents(classEntity.getStudents())).build();
		return classResponse;
	}

	private ClassEntity getClassFromDB(String id) {
		Optional<ClassEntity> optionalClass = classRepo.findById(id);

		if (optionalClass.isEmpty()) {
			return null;
		}

		return optionalClass.get();
	}

	private Set<StudentResponse> populateStudents(Set<StudentEntity> students) {
		Set<StudentResponse> list = new HashSet<StudentResponse>();
		if (students == null || students.isEmpty()) {
			return list;
		}
		for (StudentEntity entity : students) {
			list.add(assignStudent(entity));
		}
		return list;
	}

	private StudentResponse assignStudent(StudentEntity entity) {
		if (entity == null) {
			return null;
		}
		return StudentResponse.builder().id(entity.getId()).build();
	}

	public List<ClassResponse> getClassListByStd(StdEnum std, String schoolId) throws SchoolException {
		log.info("Fetching classes for std: {} in school: {} started...", std, schoolId);
		try {

			List<ClassEntity> classEntities = getClassesForStdFromDB(std, schoolId);

			List<ClassResponse> response = populateClassResponse(classEntities);
			log.info("Fetching classes for std: {} in school: {} completed!!!", std, schoolId);

			return response;
		} catch (Exception exception) {
			log.error("Fetching classes for std: {} in school: {} has encountered an error: {}", std, schoolId,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private List<ClassResponse> populateClassResponse(List<ClassEntity> classEntities) {
		List<ClassResponse> list = new ArrayList<ClassResponse>();
		if (classEntities == null || classEntities.isEmpty()) {
			return list;
		}
		for (ClassEntity classEntity : classEntities) {
			list.add(convertClassEntityToResponse(classEntity));
		}
		return list;
	}

	private List<ClassEntity> getClassesForStdFromDB(StdEnum std, String schoolId) {
		return classRepo.findAllBySchoolAndStd(schoolId, std);
	}

	public ClassResponse assignStaff(String id, String staff, String username, String schoolId) throws SchoolException {
		log.info("Assigning staff: {} to class: {} started...", staff, id);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You are not authorised person");
			}

			if (!checkStaffExists(schoolId, staff)) {
				throw new SchoolException("This staff is not valid");
			}

			ClassResponse response = updateStaffInDB(id, staff);

			log.info("Assigning staff: {} to class: {} completed!!!", staff, id);
			return response;
		} catch (Exception exception) {
			log.error("Assigning staff: {} to class: {} has encountered an error: {}", staff, id,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private ClassResponse updateStaffInDB(String id, String staff) {
		classRepo.updateStaff(id, staff);
		return convertClassEntityToResponse(classRepo.findById(id).get());
	}

	public ClassResponse enrollStudent(String id, String student, String username, String schoolId)
			throws SchoolException {
		log.info("Enrolling student: {} to class: {} started...", student, id);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You are not authorised person");
			}

			if (!checkStudentInSchool(schoolId, student)) {
				throw new SchoolException("This student is not valid");
			}

			ClassResponse response = updateClassWithNewStudent(id, student);

			return response;

		} catch (Exception exception) {
			log.error("Enrolling student: {} to class: {} has encountered an error: {}", student, id,
					exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	private ClassResponse updateClassWithNewStudent(String id, String student) {
		log.info("Adding student: {} into class: {}", student, id);
		Optional<ClassEntity> optionalClass = classRepo.findById(id);
		if (optionalClass.isEmpty() || optionalClass.get() == null) {
			return ClassResponse.builder().build();
		}

		ClassEntity classEntity = optionalClass.get();
		StudentEntity studentEntity = fetchStduentFromDB(student);

		classEntity.getStudents().add(studentEntity);

		classRepo.save(classEntity);

		return convertClassEntityToResponse(classEntity);

	}

	private StudentEntity fetchStduentFromDB(String student) {
		Optional<StudentEntity> optionalStudent = studentRepo.findById(student);
		if (optionalStudent.isEmpty() || optionalStudent.get() == null) {
			return null;
		}
		return optionalStudent.get();
	}

	private boolean checkStudentInSchool(String schoolId, String student) {

		log.info("Checking whether the student: {} belongs to school: {} or not", student, schoolId);

		if (student == null) {
			return false;
		}

		StudentEntity studentEntity = fetchStduentFromDB(student);

		return studentEntity != null && studentEntity.getSchool().equals(schoolId);
	}

	public ClassResponse updateClassInfo(String id, ClassRegRequest request, String username, String schoolId)
			throws SchoolException {
		log.info("Updating class: {} started...", id);
		try {

			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You are not authorised person");
			}

			ClassEntity classEntity = getClassFromDB(id);

			classEntity.setStd(request.getStd());
			classEntity.setSubject(request.getSubject());
			classEntity.setStart(request.getStart());
			classEntity.setEnd(request.getEnd());
			classEntity.setRecurring(request.isRecurring());

			classRepo.save(classEntity);

			log.info("Updating class: {} completed!!!", id);

			return convertClassEntityToResponse(classEntity);

		} catch (Exception exception) {
			log.error("Updating class: {} has encountered an error: {}", id, exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

	public void deleteClass(String id, String username, String schoolId) throws SchoolException {
		log.info("Deleting class: {} started...", id);
		try {
			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You are not authorised person");
			}

			classRepo.deleteById(id);

			log.info("Deleting class: {} completed!!!", id);

		}

		catch (Exception exception) {
			log.info("Deleting class: {} has encountered an error: {}", id, exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}

	}

	public ClassResponse renew(String id, String username, String schoolId) throws SchoolException {
		log.info("Renewing class: {} started...", id);
		try {
			if (!checkAdminAndSchoolExists(username, schoolId)) {
				throw new SchoolException("You are not authorised person");
			}

			ClassEntity  classEntity = getClassFromDB(id);
			classEntity.setStaff("");
			classEntity.setStudents(new HashSet<StudentEntity>());
			
			classRepo.save(classEntity);

			log.info("Renewing class: {} completed!!!", id);
			
			return convertClassEntityToResponse(classEntity);

		}

		catch (Exception exception) {
			log.info("Renewing class: {} has encountered an error: {}", id, exception.getMessage());
			throw new SchoolException(exception.getMessage());
		}
	}

}
