# 🎓 Student Record Management System

A simple, robust, and clean **RESTful Web Application** built for student record management. Designed strictly according to academic guidelines using **Spring Boot**, **MySQL**, **Maven**, and tested with **Postman** and **Eclipse IDE**, adhering to a minimal and easy-to-understand file structure.

---

## 🛠️ Required Softwares

1. **Eclipse IDE** (Eclipse IDE for Java Developers / Enterprise Java)
2. **Apache Maven** (Embedded in Eclipse, or via included `mvnw.cmd` wrapper)
3. **MySQL Server** (MySQL 5.5, 5.7, 8.0, or newer)
4. **Postman** (For testing REST API endpoints)
5. **Java Development Kit (JDK)** 17, 21, or 25

---

## 📂 Project Structure

This project follows the **simple files format** requested by academic guidelines — no redundant layers or over-engineering:

```text
StudentRecordManagement/
├── .mvn/                                     # Maven Wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── student/
│   │   │           ├── StudentManagementApplication.java # Spring Boot entry point
│   │   │           ├── controller/
│   │   │           │   └── StudentController.java        # REST Controller (GET, POST, PUT, DELETE)
│   │   │           ├── model/
│   │   │           │   └── Student.java                  # Model / POJO class
│   │   │           └── repository/
│   │   │               └── StudentRepository.java        # JDBC Template database operations
│   │   └── resources/
│   │       ├── application.properties                    # Server port and MySQL credentials
│   │       └── static/
│   │           └── index.html                            # Interactive web dashboard
├── .classpath                                # Eclipse Classpath metadata
├── .gitignore                                # Git ignore file
├── .project                                  # Eclipse Project metadata
├── mvnw                                      # Linux/Mac Maven wrapper script
├── mvnw.cmd                                  # Windows Maven wrapper script
├── pom.xml                                   # Maven dependencies and build configuration
├── schema.sql                                # MySQL database and table setup script
├── StudentRecordManagement.postman_collection.json # Pre-configured Postman API collection
└── README.md                                 # Complete documentation & run guide
```

---

## 🚀 Setup & Execution Guide

### 1. MySQL Database Setup

1. Start your local MySQL service (via Services or XAMPP/WAMP/MySQL Workbench).
2. Open your terminal or MySQL Command Line Client and execute `schema.sql`:
   ```sql
   mysql -u root -p < schema.sql
   ```
   *(Or open `schema.sql` in MySQL Workbench / phpMyAdmin and run the script).*
3. Verify your database settings in [`src/main/resources/application.properties`](file:///i:/ACEDEMICS/StudentRecordManagement/src/main/resources/application.properties):
   ```properties
   server.port=8080
   spring.datasource.url=jdbc:mysql://localhost:3306/student_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=qwertyuiop
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```
   *(Update username or password if your MySQL configuration differs).*

---

### 2. How to Run in Eclipse IDE

1. Open **Eclipse IDE**.
2. Click **File** ➡️ **Import...**
3. Select **Maven** ➡️ **Existing Maven Projects**, then click **Next**.
4. Click **Browse...** and select the folder `StudentRecordManagement`.
5. Ensure `pom.xml` is checked, then click **Finish**.
6. Wait a few seconds for Eclipse to import and resolve dependencies.
7. In the Project Explorer, navigate to:  
   `src/main/java` ➡️ `com.student` ➡️ **`StudentManagementApplication.java`**.
8. Right-click `StudentManagementApplication.java` ➡️ **Run As** ➡️ **Java Application** (or **Spring Boot App**).
9. Look at the Eclipse Console window to confirm startup:
   ```text
   =========================================================
     🎓 Student Record Management System Started Successfully!
     🌐 Web Dashboard: http://localhost:8080/
     📡 REST API Base: http://localhost:8080/api/students
   =========================================================
   ```

---

### 3. How to Run via Command Line (Maven)

Open your command prompt or terminal inside the project directory:

- **Using Windows Maven Wrapper (No installation needed):**
  ```cmd
  .\mvnw.cmd spring-boot:run
  ```
- **Using Installed Maven:**
  ```cmd
  mvn spring-boot:run
  ```

---

### 4. Testing with Postman

We have included a ready-to-use Postman Collection:  
[`StudentRecordManagement.postman_collection.json`](file:///i:/ACEDEMICS/StudentRecordManagement/StudentRecordManagement.postman_collection.json).

#### How to Import into Postman:
1. Open **Postman**.
2. Click the **Import** button (top-left).
3. Drag & drop or select `StudentRecordManagement.postman_collection.json`.
4. You will see the **Student Record Management API** collection with all pre-configured requests!

#### REST API Endpoints Reference:

| Operation | HTTP Method | Endpoint | Description |
|:---|:---:|:---|:---|
| **Get All** | `GET` | `http://localhost:8080/api/students` | Retrieve all student records |
| **Get by ID** | `GET` | `http://localhost:8080/api/students/1` | Retrieve a single student by ID |
| **Create** | `POST` | `http://localhost:8080/api/students` | Add a new student record |
| **Update** | `PUT` | `http://localhost:8080/api/students/1` | Update an existing student |
| **Delete** | `DELETE` | `http://localhost:8080/api/students/1` | Remove a student record |

#### Sample JSON Payloads for Postman:

- **Create Student (POST `/api/students`)**:
  ```json
  {
    "name": "Sameer Ahmed",
    "roll": "CSE-2024-099",
    "department": "Computer Science",
    "year": "3rd Year",
    "email": "sameer@example.com",
    "phone": "9812345678"
  }
  ```

- **Update Student (PUT `/api/students/1`)**:
  ```json
  {
    "name": "Mohammed Imran (Updated)",
    "roll": "CSE-2024-001",
    "department": "Computer Science & Engineering",
    "year": "4th Year",
    "email": "imran.updated@example.com",
    "phone": "9876543210"
  }
  ```

---

### 5. Accessing the Web Dashboard

While the backend is running, open any web browser and visit:
👉 **[http://localhost:8080/](http://localhost:8080/)**

**Features:**
- Real-time student directory with live search filtering
- Form to add new students with instant client & server validation
- Edit and update existing student records
- Confirmation modal before record deletion
- Automatic synchronization with MySQL database via REST API