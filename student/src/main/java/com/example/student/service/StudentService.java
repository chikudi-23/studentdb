package com.example.student.service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.util.List;
import java.util.Optional;

@Service
@Validated // Enables method-level validation
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class); // Initialize Logger

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.info("StudentService initialized."); // Example log
    }

    public List<Student> getAllStudents() {
        logger.debug("Fetching all students from repository.");
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        logger.debug("Fetching student by ID: {}", id);
        return studentRepository.findById(id);
    }

    public Student createStudent(@Valid Student student) {
        logger.debug("Saving new student to repository: {}", student.getName());
        return studentRepository.save(student);
    }

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

    public void deleteStudent(Long id) {
        logger.debug("Deleting student with ID: {}", id);
        studentRepository.deleteById(id);
        logger.info("Student with ID: {} deleted.", id);
    }

    public List<Student> findStudentsByName(String name) {
        logger.debug("Searching students by name containing: {}", name);
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> findStudentsOlderThan(int age) {
        logger.debug("Searching students older than age: {}", age);
        return studentRepository.findByAgeGreaterThanNative(age);
    }
}