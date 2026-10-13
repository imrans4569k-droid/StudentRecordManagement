package com.student.controller;

import com.student.model.Student;
import com.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Student Record Management.
 * Exposes endpoints to test with Postman or interact with the frontend UI.
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * READ: Get all student records.
     * GET /api/students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return ResponseEntity.ok(students);
    }

    /**
     * READ: Get a single student by ID.
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            return ResponseEntity.ok(studentOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createMessage("error", "Student record not found with ID: " + id));
        }
    }

    /**
     * CREATE: Add a new student record.
     * POST /api/students
     */
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody(required = false) Student student,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String roll,
                                           @RequestParam(required = false) String department,
                                           @RequestParam(required = false) String year,
                                           @RequestParam(required = false) String email,
                                           @RequestParam(required = false) String phone) {

        // Fallback for form-urlencoded requests
        if (student == null) {
            student = new Student(null, name, roll, department, year, email, phone);
        }

        String validationError = validateStudent(student);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(createMessage("error", validationError));
        }

        // Check if roll number already exists
        if (studentRepository.existsByRoll(student.getRoll(), null)) {
            return ResponseEntity.badRequest()
                    .body(createMessage("error", "Roll number '" + student.getRoll() + "' already exists."));
        }

        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    /**
     * UPDATE: Update an existing student record by path variable.
     * PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudentByPath(@PathVariable Long id,
                                                 @RequestBody(required = false) Student student,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String roll,
                                                 @RequestParam(required = false) String department,
                                                 @RequestParam(required = false) String year,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String phone) {
        if (student == null) {
            student = new Student(id, name, roll, department, year, email, phone);
        } else {
            student.setId(id);
        }
        return processUpdate(student);
    }

    /**
     * UPDATE: Update an existing student record with query parameter id.
     * PUT /api/students?id={id}
     */
    @PutMapping
    public ResponseEntity<?> updateStudentByParam(@RequestParam(required = false) Long id,
                                                  @RequestBody(required = false) Student student,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String roll,
                                                  @RequestParam(required = false) String department,
                                                  @RequestParam(required = false) String year,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String phone) {
        if (student != null) {
            if (student.getId() == null && id != null) {
                student.setId(id);
            }
        } else {
            student = new Student(id, name, roll, department, year, email, phone);
        }

        if (student.getId() == null) {
            return ResponseEntity.badRequest().body(createMessage("error", "Student ID is required for update."));
        }

        return processUpdate(student);
    }

    /**
     * DELETE: Delete a student record by path variable.
     * DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudentByPath(@PathVariable Long id) {
        return processDelete(id);
    }

    /**
     * DELETE: Delete a student record with query parameter id.
     * DELETE /api/students?id={id}
     */
    @DeleteMapping
    public ResponseEntity<?> deleteStudentByParam(@RequestParam Long id) {
        return processDelete(id);
    }

    // --- Helper Validation & Processing Methods ---

    private ResponseEntity<?> processUpdate(Student student) {
        if (student.getId() == null || student.getId() <= 0) {
            return ResponseEntity.badRequest().body(createMessage("error", "A valid Student ID is required."));
        }

        Optional<Student> existingOpt = studentRepository.findById(student.getId());
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createMessage("error", "Student record not found with ID: " + student.getId()));
        }

        String validationError = validateStudent(student);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(createMessage("error", validationError));
        }

        // Check if new roll number belongs to another student
        if (studentRepository.existsByRoll(student.getRoll(), student.getId())) {
            return ResponseEntity.badRequest()
                    .body(createMessage("error", "Roll number '" + student.getRoll() + "' already exists for another student."));
        }

        boolean updated = studentRepository.update(student);
        if (updated) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createMessage("error", "Failed to update student record."));
        }
    }

    private ResponseEntity<?> processDelete(Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(createMessage("error", "A valid Student ID is required."));
        }

        Optional<Student> existingOpt = studentRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createMessage("error", "Student record not found with ID: " + id));
        }

        boolean deleted = studentRepository.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok(createMessage("message", "Student record deleted successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createMessage("error", "Failed to delete student record."));
        }
    }

    private String validateStudent(Student s) {
        if (s.getName() == null || s.getName().trim().isEmpty()) {
            return "Student name is required.";
        }
        if (s.getRoll() == null || s.getRoll().trim().isEmpty()) {
            return "Roll number is required.";
        }
        if (s.getDepartment() == null || s.getDepartment().trim().isEmpty()) {
            return "Department is required.";
        }
        if (s.getYear() == null || s.getYear().trim().isEmpty()) {
            return "Year is required.";
        }
        if (s.getEmail() == null || s.getEmail().trim().isEmpty()) {
            return "Email is required.";
        }
        if (s.getPhone() == null || s.getPhone().trim().isEmpty()) {
            return "Phone number is required.";
        }
        return null;
    }

    private Map<String, String> createMessage(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
