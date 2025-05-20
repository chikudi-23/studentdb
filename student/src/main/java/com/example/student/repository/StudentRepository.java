package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), deleteById()

    // Custom JPQL query to find students by name (case-insensitive)
    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByNameContainingIgnoreCase(@Param("name") String name);

    // Custom Native SQL query to find students older than a certain age
    @Query(value = "SELECT * FROM Student WHERE age > :age", nativeQuery = true)
    List<Student> findByAgeGreaterThanNative(@Param("age") int age);
}