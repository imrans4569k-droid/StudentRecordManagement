package com.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Student Record Management System.
 * Run this class in Eclipse (Run As -> Java Application) or via Maven.
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
        System.out.println("=========================================================");
        System.out.println("  🎓 Student Record Management System Started Successfully!");
        System.out.println("  🌐 Web Dashboard: http://localhost:8080/");
        System.out.println("  📡 REST API Base: http://localhost:8080/api/students");
        System.out.println("=========================================================");
    }
}
