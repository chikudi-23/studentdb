package com.example.student.controller;

import com.example.student.model.Student;
import com.example.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class); // Initialize Logger

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
        logger.info("StudentController initialized."); // Example log
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        logger.info("Fetching all students.");
        List<Student> students = studentService.getAllStudents();
        logger.debug("Found {} students.", students.size());
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        logger.info("Fetching student with ID: {}", id);
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            logger.debug("Found student with ID: {}", id);
            return new ResponseEntity<>(student.get(), HttpStatus.OK);
        } else {
            logger.warn("Student with ID: {} not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStudent(@Valid @RequestBody Student student) {
        logger.info("Creating new student: {}", student.getName());
        Student createdStudent = studentService.createStudent(student);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student created successfully");
        response.put("status", HttpStatus.CREATED.value());
        response.put("studentId", createdStudent.getId());
        logger.info("Student created successfully with ID: {}", createdStudent.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @Valid @RequestBody Student updatedStudent) {
        logger.info("Updating student with ID: {}", id);
        Optional<Student> student = studentService.updateStudent(id, updatedStudent);
        Map<String, Object> response = new HashMap<>();
        if (student.isPresent()) {
            response.put("message", "Student updated successfully");
            response.put("status", HttpStatus.OK.value());
            logger.info("Student with ID: {} updated successfully.", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Student not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            logger.warn("Student with ID: {} not found for update.", id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        logger.info("Deleting student with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        if (studentService.getStudentById(id).isPresent()) {
            studentService.deleteStudent(id);
            response.put("message", "Student deleted successfully");
            response.put("status", HttpStatus.OK.value());
            logger.info("Student with ID: {} deleted successfully.", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Student not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            logger.warn("Student with ID: {} not found for deletion.", id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> findStudentsByName(@RequestParam String name) {
        logger.info("Searching students by name: {}", name);
        List<Student> students = studentService.findStudentsByName(name);
        logger.debug("Found {} students with name containing '{}'.", students.size(), name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/search/older-than")
    public ResponseEntity<List<Student>> findStudentsOlderThan(@RequestParam int age) {
        logger.info("Searching students older than age: {}", age);
        List<Student> students = studentService.findStudentsOlderThan(age);
        logger.debug("Found {} students older than age {}.", students.size(), age);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}