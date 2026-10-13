package com.student.repository;

import com.student.model.Student;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Student Repository Class.
 * Handles all database CRUD operations on MySQL using Spring's JdbcTemplate.
 */
@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper to convert MySQL ResultSet rows into Student objects
    private final RowMapper<Student> rowMapper = (rs, rowNum) -> new Student(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("roll"),
            rs.getString("department"),
            rs.getString("year"),
            rs.getString("email"),
            rs.getString("phone")
    );

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieve all students ordered by ID descending.
     */
    public List<Student> findAll() {
        String sql = "SELECT id, name, roll, department, year, email, phone FROM students ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Find a student by ID.
     */
    public Optional<Student> findById(Long id) {
        String sql = "SELECT id, name, roll, department, year, email, phone FROM students WHERE id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Find a student by roll number.
     */
    public Optional<Student> findByRoll(String roll) {
        String sql = "SELECT id, name, roll, department, year, email, phone FROM students WHERE roll = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, roll);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Check if a roll number already exists for a different student ID.
     */
    public boolean existsByRoll(String roll, Long excludeId) {
        String sql;
        Integer count;
        if (excludeId != null && excludeId > 0) {
            sql = "SELECT COUNT(*) FROM students WHERE roll = ? AND id <> ?";
            count = jdbcTemplate.queryForObject(sql, Integer.class, roll, excludeId);
        } else {
            sql = "SELECT COUNT(*) FROM students WHERE roll = ?";
            count = jdbcTemplate.queryForObject(sql, Integer.class, roll);
        }
        return count != null && count > 0;
    }

    /**
     * Insert a new student record into MySQL and return the created student with generated ID.
     */
    public Student save(Student student) {
        String sql = "INSERT INTO students (name, roll, department, year, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getName());
            ps.setString(2, student.getRoll());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getYear());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getPhone());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            student.setId(keyHolder.getKey().longValue());
        }
        return student;
    }

    /**
     * Update an existing student record in MySQL.
     */
    public boolean update(Student student) {
        String sql = "UPDATE students SET name = ?, roll = ?, department = ?, year = ?, email = ?, phone = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(
                sql,
                student.getName(),
                student.getRoll(),
                student.getDepartment(),
                student.getYear(),
                student.getEmail(),
                student.getPhone(),
                student.getId()
        );
        return rowsAffected > 0;
    }

    /**
     * Delete a student by ID.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
}
