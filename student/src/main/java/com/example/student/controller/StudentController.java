package com.example.student.controller;

import com.example.student.model.Student;
import com.example.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStudent(@Valid @RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student created successfully");
        response.put("status", HttpStatus.CREATED.value());
        response.put("studentId", createdStudent.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @Valid @RequestBody Student updatedStudent) {
        Optional<Student> student = studentService.updateStudent(id, updatedStudent);
        Map<String, Object> response = new HashMap<>();
        if (student.isPresent()) {
            response.put("message", "Student updated successfully");
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Student not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (studentService.getStudentById(id).isPresent()) {
            studentService.deleteStudent(id);
            response.put("message", "Student deleted successfully");
            response.put("status", HttpStatus.OK.value()); // Changed to HttpStatus.OK
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Student not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Student>> findStudentsByName(@RequestParam String name) {
        List<Student> students = studentService.findStudentsByName(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/search/older-than")
    public ResponseEntity<List<Student>> findStudentsOlderThan(@RequestParam int age) {
        List<Student> students = studentService.findStudentsOlderThan(age);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}