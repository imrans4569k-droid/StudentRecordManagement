import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static final int PORT = 8080;

    // This file is created automatically when a student is saved.
    private static final Path DATABASE_FILE = Path.of("students.db");

    private static final List<Student> students = new ArrayList<>();

    private static long nextId = 1;

    // Student data model.
    static class Student {
        long id;
        String name;
        String roll;
        String department;
        String year;
        String email;
        String phone;

        Student(
                long id,
                String name,
                String roll,
                String department,
                String year,
                String email,
                String phone
        ) {
            this.id = id;
            this.name = name;
            this.roll = roll;
            this.department = department;
            this.year = year;
            this.email = email;
            this.phone = phone;
        }
    }

    public static void main(String[] args) throws Exception {

        // Restore previously saved student records.
        loadDatabase();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT),
                0
        );

        // Both the webpage and API are served by this Java application.
        server.createContext("/", App::handleRequest);

        server.start();

        System.out.println("======================================");
        System.out.println(" Student Record Management System");
        System.out.println("======================================");
        System.out.println("Server running at: http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop the server.");
    }

    // Handle webpage requests and API requests.
    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/api/students")) {
                handleStudentApi(exchange);
                return;
            }

            // Serve the frontend.
            if (path.equals("/") || path.equals("/index.html")) {

                if (!exchange.getRequestMethod().equals("GET")) {
                    sendJson(
                            exchange,
                            405,
                            errorJson("Method not allowed")
                    );
                    return;
                }

                Path htmlFile = Path.of("index.html");

                if (!Files.exists(htmlFile)) {
                    sendJson(
                            exchange,
                            500,
                            errorJson("index.html was not found in this folder")
                    );
                    return;
                }

                byte[] html = Files.readAllBytes(htmlFile);

                exchange.getResponseHeaders().set(
                        "Content-Type",
                        "text/html; charset=UTF-8"
                );

                exchange.sendResponseHeaders(200, html.length);

                try (OutputStream output = exchange.getResponseBody()) {
                    output.write(html);
                }

                return;
            }

            sendJson(exchange, 404, errorJson("Page not found"));

        } catch (Exception exception) {

            try {
                String message = exception.getMessage();

                if (message == null || message.isBlank()) {
                    message = "Internal server error";
                }

                sendJson(exchange, 500, errorJson(message));

            } catch (Exception ignored) {
                // The connection may already have closed.
            }
        }
    }

    // Student CRUD API.
    private static void handleStudentApi(HttpExchange exchange)
            throws IOException {

        String method = exchange.getRequestMethod();

        Map<String, String> parameters = parseParameters(
                exchange.getRequestURI().getRawQuery()
        );

        // POST and PUT send form data in the request body.
        if (method.equals("POST") || method.equals("PUT")) {

            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            parameters.putAll(parseParameters(body));
        }

        // READ: Return every student.
        if (method.equals("GET")) {

            sendJson(exchange, 200, studentsToJson());

            return;
        }

        // CREATE: Add a new student.
        if (method.equals("POST")) {

            Student student = studentFrom(parameters);

            String validationError = validateStudent(student);

            if (validationError != null) {
                sendJson(
                        exchange,
                        400,
                        errorJson(validationError)
                );
                return;
            }

            if (rollExists(student.roll, -1)) {
                sendJson(
                        exchange,
                        400,
                        errorJson("This roll number already exists.")
                );
                return;
            }

            student.id = nextId++;

            students.add(student);

            saveDatabase();

            sendJson(exchange, 201, studentToJson(student));

            return;
        }

        // UPDATE: Modify an existing student.
        if (method.equals("PUT")) {

            long id = getId(parameters);

            if (id < 0) {
                sendJson(
                        exchange,
                        400,
                        errorJson("A valid student ID is required.")
                );
                return;
            }

            Student existing = findStudent(id);

            if (existing == null) {
                sendJson(
                        exchange,
                        404,
                        errorJson("Student record not found.")
                );
                return;
            }

            Student updated = studentFrom(parameters);

            String validationError = validateStudent(updated);

            if (validationError != null) {
                sendJson(
                        exchange,
                        400,
                        errorJson(validationError)
                );
                return;
            }

            if (rollExists(updated.roll, id)) {
                sendJson(
                        exchange,
                        400,
                        errorJson("This roll number already exists.")
                );
                return;
            }

            updated.id = id;

            int index = students.indexOf(existing);

            students.set(index, updated);

            saveDatabase();

            sendJson(exchange, 200, studentToJson(updated));

            return;
        }

        // DELETE: Remove a student.
        if (method.equals("DELETE")) {

            long id = getId(parameters);

            if (id < 0) {
                sendJson(
                        exchange,
                        400,
                        errorJson("A valid student ID is required.")
                );
                return;
            }

            Student student = findStudent(id);

            if (student == null) {
                sendJson(
                        exchange,
                        404,
                        errorJson("Student record not found.")
                );
                return;
            }

            students.remove(student);

            saveDatabase();

            sendJson(
                    exchange,
                    200,
                    "{\"success\":true}"
            );

            return;
        }

        sendJson(
                exchange,
                405,
                errorJson("Method not allowed")
        );
    }

    // Read a student from submitted form parameters.
    private static Student studentFrom(Map<String, String> parameters) {

        return new Student(
                0,
                clean(parameters.get("name")),
                clean(parameters.get("roll")),
                clean(parameters.get("department")),
                clean(parameters.get("year")),
                clean(parameters.get("email")),
                clean(parameters.get("phone"))
        );
    }

    // Validate required fields.
    private static String validateStudent(Student student) {

        if (student.name.isBlank()) {
            return "Student name is required.";
        }

        if (student.roll.isBlank()) {
            return "Roll number is required.";
        }

        if (student.name.length() > 100) {
            return "Student name is too long.";
        }

        if (student.roll.length() > 40) {
            return "Roll number is too long.";
        }

        if (student.department.length() > 100) {
            return "Department name is too long.";
        }

        if (student.year.length() > 30) {
            return "Academic year is too long.";
        }

        if (student.email.length() > 150) {
            return "Email address is too long.";
        }

        if (student.phone.length() > 25) {
            return "Phone number is too long.";
        }

        return null;
    }

    // Check whether another student already has this roll number.
    private static boolean rollExists(String roll, long excludedId) {

        for (Student student : students) {

            if (student.id != excludedId
                    && student.roll.equalsIgnoreCase(roll)) {

                return true;
            }
        }

        return false;
    }

    // Find a student by ID.
    private static Student findStudent(long id) {

        for (Student student : students) {

            if (student.id == id) {
                return student;
            }
        }

        return null;
    }

    // Get the ID from request parameters.
    private static long getId(Map<String, String> parameters) {

        try {
            return Long.parseLong(parameters.get("id"));
        } catch (Exception exception) {
            return -1;
        }
    }

    // Convert URL-encoded form data into key-value pairs.
    private static Map<String, String> parseParameters(String rawData) {

        Map<String, String> parameters = new HashMap<>();

        if (rawData == null || rawData.isBlank()) {
            return parameters;
        }

        for (String part : rawData.split("&")) {

            String[] pair = part.split("=", 2);

            String key = URLDecoder.decode(
                    pair[0],
                    StandardCharsets.UTF_8
            );

            String value = pair.length > 1
                    ? URLDecoder.decode(
                            pair[1],
                            StandardCharsets.UTF_8
                    )
                    : "";

            parameters.put(key, value);
        }

        return parameters;
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    // Find all student records and convert them to JSON.
    private static String studentsToJson() {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < students.size(); i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(studentToJson(students.get(i)));
        }

        json.append("]");

        return json.toString();
    }

    // Convert one student to JSON.
    private static String studentToJson(Student student) {

        return "{"
                + "\"id\":" + student.id + ","
                + "\"name\":" + jsonString(student.name) + ","
                + "\"roll\":" + jsonString(student.roll) + ","
                + "\"department\":" + jsonString(student.department) + ","
                + "\"year\":" + jsonString(student.year) + ","
                + "\"email\":" + jsonString(student.email) + ","
                + "\"phone\":" + jsonString(student.phone)
                + "}";
    }

    // Safely escape text for JSON.
    private static String jsonString(String value) {

        StringBuilder result = new StringBuilder("\"");

        for (char character : value.toCharArray()) {

            switch (character) {

                case '"':
                    result.append("\\\"");
                    break;

                case '\\':
                    result.append("\\\\");
                    break;

                case '\b':
                    result.append("\\b");
                    break;

                case '\f':
                    result.append("\\f");
                    break;

                case '\n':
                    result.append("\\n");
                    break;

                case '\r':
                    result.append("\\r");
                    break;

                case '\t':
                    result.append("\\t");
                    break;

                default:

                    if (character < 0x20) {
                        result.append(
                                String.format(
                                        "\\u%04x",
                                        (int) character
                                )
                        );
                    } else {
                        result.append(character);
                    }
            }
        }

        result.append("\"");

        return result.toString();
    }

    private static String errorJson(String message) {

        return "{\"error\":" + jsonString(message) + "}";
    }

    // Send a JSON response to the frontend.
    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    // Encode text so each student field is stored safely on one line.
    private static String encode(String value) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }

    private static String decode(String value) {

        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // Save all students to the local database file.
    private static void saveDatabase() throws IOException {

        List<String> lines = new ArrayList<>();

        for (Student student : students) {

            String line =
                    student.id + ","
                    + encode(student.name) + ","
                    + encode(student.roll) + ","
                    + encode(student.department) + ","
                    + encode(student.year) + ","
                    + encode(student.email) + ","
                    + encode(student.phone);

            lines.add(line);
        }

        Files.write(
                DATABASE_FILE,
                lines,
                StandardCharsets.UTF_8
        );
    }

    // Load previously saved students when Java starts.
    private static void loadDatabase() throws IOException {

        if (!Files.exists(DATABASE_FILE)) {
            return;
        }

        List<String> lines = Files.readAllLines(
                DATABASE_FILE,
                StandardCharsets.UTF_8
        );

        for (String line : lines) {

            try {
                String[] parts = line.split(",", -1);

                if (parts.length != 7) {
                    continue;
                }

                Student student = new Student(
                        Long.parseLong(parts[0]),
                        decode(parts[1]),
                        decode(parts[2]),
                        decode(parts[3]),
                        decode(parts[4]),
                        decode(parts[5]),
                        decode(parts[6])
                );

                students.add(student);

                nextId = Math.max(nextId, student.id + 1);

            } catch (Exception exception) {

                System.err.println(
                        "Skipping an invalid student record."
                );
            }
        }

        System.out.println(
                "Loaded " + students.size() + " student record(s)."
        );
    }
}