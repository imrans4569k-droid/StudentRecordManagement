# Student Record Management System

A simple web-based application to manage student records. It features a Java-based backend using the built-in `HttpServer` and a simple HTML/JS frontend. Data is saved locally in a `students.db` file.

## Prerequisites

- **Java Development Kit (JDK)** 11 or higher installed on your system.

## How to Run

1. **Clone the repository** (if you haven't already):
   ```bash
   git clone https://github.com/imrans4569k-droid/StudentRecordManagement.git
   cd StudentRecordManagement
   ```

2. **Compile the Java application**:
   ```bash
   javac app.java
   ```
   *(This will generate `App.class` and `App$Student.class` files in the directory)*

3. **Run the application**:
   ```bash
   java App
   ```

4. **Access the application**:
   Open your web browser and navigate to:
   [http://localhost:8080](http://localhost:8080)

## How to Stop

To stop the server, go to the terminal or command prompt where the application is running and press `Ctrl+C`.

## Features
- View all student records
- Add a new student record
- Update existing student records
- Delete student records
- Local persistent storage (`students.db`)