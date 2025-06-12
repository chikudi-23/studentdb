package com.example.student.service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig; // Import CacheConfig
import org.springframework.cache.annotation.CacheEvict; // Import CacheEvict
import org.springframework.cache.annotation.Cacheable; // Import Cacheable
import org.springframework.cache.annotation.CachePut; // Import CachePut


import java.util.List;
import java.util.Optional;

@Service
@Validated // Enables method-level validation
@CacheConfig(cacheNames = "students") // Define a common cache name for this service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.info("StudentService initialized.");
    }

    @Cacheable(cacheNames = "students") // Cache all students
    public List<Student> getAllStudents() {
        logger.debug("Fetching all students from repository.");
        return studentRepository.findAll();
    }

    @Cacheable(cacheNames = "studentById", key = "#id") // Cache student by ID
    public Optional<Student> getStudentById(Long id) {
        logger.debug("Fetching student by ID: {}", id);
        return studentRepository.findById(id);
    }

    @CacheEvict(cacheNames = {"students", "studentById", "studentsByName", "studentsOlderThan"}, allEntries = true) // Evict all caches on creation
    public Student createStudent(@Valid Student student) {
        logger.debug("Saving new student to repository: {}", student.getName());
        return studentRepository.save(student);
    }

    @CachePut(cacheNames = "studentById", key = "#id") // Update cache with the new student after successful update
    @CacheEvict(cacheNames = {"students", "studentsByName", "studentsOlderThan"}, allEntries = true, beforeInvocation = true) // Evict relevant caches before update
    public Optional<Student> updateStudent(Long id, @Valid Student updatedStudent) {
        logger.debug("Attempting to update student with ID: {}", id);
        return studentRepository.findById(id)
                .map(student -> {
                    logger.debug("Student found with ID: {}. Updating details.", id);
                    student.setName(updatedStudent.getName());
                    student.setEmail(updatedStudent.getEmail());
                    student.setAge(updatedStudent.getAge());
                    return studentRepository.save(student);
                })
                .or(() -> {
                    logger.warn("Student with ID: {} not found for update.", id);
                    return Optional.empty();
                });
    }

    @CacheEvict(cacheNames = {"students", "studentById", "studentsByName", "studentsOlderThan"}, allEntries = true) // Evict all caches on deletion
    public void deleteStudent(Long id) {
        logger.debug("Deleting student with ID: {}", id);
        studentRepository.deleteById(id);
        logger.info("Student with ID: {} deleted.", id);
    }

    @Cacheable(cacheNames = "studentsByName", key = "#name") // Cache students by name
    public List<Student> findStudentsByName(String name) {
        logger.debug("Searching students by name containing: {}", name);
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    @Cacheable(cacheNames = "studentsOlderThan", key = "#age") // Cache students older than a certain age
    public List<Student> findStudentsOlderThan(int age) {
        logger.debug("Searching students older than age: {}", age);
        return studentRepository.findByAgeGreaterThanNative(age);
    }
}