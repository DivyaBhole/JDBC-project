package com.example.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HostelManagementSystem {

    private static final String URL = "jdbc:mysql://localhost:3306/hostel_management";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; 

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the database!");

            while (true) {
                System.out.println("Menu:");
                System.out.println("1. Add Student");
                System.out.println("2. Add Room");
                System.out.println("3. Book Room");
                System.out.println("4. Display Bookings");
                System.out.println("5. Update Student");
                System.out.println("6. Update Room");
                System.out.println("7. Delete Student");
                System.out.println("8. Delete Room");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter student name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter student email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter student phone: ");
                        String phone = scanner.nextLine();
                        addStudent(connection, name, email, phone);
                        break;
                    case 2:
                        System.out.print("Enter room number: ");
                        String roomNumber = scanner.nextLine();
                        System.out.print("Enter room capacity: ");
                        int capacity = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        addRoom(connection, roomNumber, capacity);
                        break;
                    case 3:
                        System.out.print("Enter student ID: ");
                        int studentId = scanner.nextInt();
                        System.out.print("Enter room ID: ");
                        int roomId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        if (studentExists(connection, studentId) && roomExists(connection, roomId)) {
                            bookRoom(connection, studentId, roomId);
                        } else {
                            System.out.println("Invalid student ID or room ID. Please try again.");
                        }
                        break;
                    case 4:
                        displayBookings(connection);
                        break;
                    case 5:
                        System.out.print("Enter student ID to update: ");
                        int updateStudentId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter new student name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new student email: ");
                        String newEmail = scanner.nextLine();
                        System.out.print("Enter new student phone: ");
                        String newPhone = scanner.nextLine();
                        updateStudent(connection, updateStudentId, newName, newEmail, newPhone);
                        break;
                    case 6:
                        System.out.print("Enter room ID to update: ");
                        int updateRoomId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter new room number: ");
                        String newRoomNumber = scanner.nextLine();
                        System.out.print("Enter new room capacity: ");
                        int newCapacity = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        updateRoom(connection, updateRoomId, newRoomNumber, newCapacity);
                        break;
                    case 7:
                        System.out.print("Enter student ID to delete: ");
                        int deleteStudentId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        deleteStudent(connection, deleteStudentId);
                        break;
                    case 8:
                        System.out.print("Enter room ID to delete: ");
                        int deleteRoomId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        deleteRoom(connection, deleteRoomId);
                        break;
                    case 9:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addStudent(Connection connection, String name, String email, String phone) throws SQLException {
        String query = "INSERT INTO students (name, email, phone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    private static void addRoom(Connection connection, String roomNumber, int capacity) throws SQLException {
        String query = "INSERT INTO rooms (room_number, capacity) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            stmt.setInt(2, capacity);
            stmt.executeUpdate();
            System.out.println("Room added successfully!");
        }
    }

    private static void bookRoom(Connection connection, int studentId, int roomId) throws SQLException {
        String query = "INSERT INTO bookings (student_id, room_id, booking_date) VALUES (?, ?, CURDATE())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
            System.out.println("Room booked successfully!");
        }
    }

    private static boolean studentExists(Connection connection, int studentId) throws SQLException {
        String query = "SELECT 1 FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean roomExists(Connection connection, int roomId) throws SQLException {
        String query = "SELECT 1 FROM rooms WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void displayBookings(Connection connection) throws SQLException {
        String query = "SELECT bookings.booking_id, students.name, rooms.room_number, bookings.booking_date "
                     + "FROM bookings "
                     + "JOIN students ON bookings.student_id = students.student_id "
                     + "JOIN rooms ON bookings.room_id = rooms.room_id";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String studentName = rs.getString("name");
                String roomNumber = rs.getString("room_number");
                String bookingDate = rs.getString("booking_date");

                System.out.printf("Booking ID: %d, Student Name: %s, Room Number: %s, Booking Date: %s%n",
                                  bookingId, studentName, roomNumber, bookingDate);
            }
        }
    }

    private static void updateStudent(Connection connection, int studentId, String name, String email, String phone) throws SQLException {
        String query = "UPDATE students SET name = ?, email = ?, phone = ? WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, studentId);
            stmt.executeUpdate();
            System.out.println("Student updated successfully!");
        }
    }

    private static void updateRoom(Connection connection, int roomId, String roomNumber, int capacity) throws SQLException {
        String query = "UPDATE rooms SET room_number = ?, capacity = ? WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            stmt.setInt(2, capacity);
            stmt.setInt(3, roomId);
            stmt.executeUpdate();
            System.out.println("Room updated successfully!");
        }
    }

    private static void deleteStudent(Connection connection, int studentId) throws SQLException {
        String query = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
            System.out.println("Student deleted successfully!");
        }
    }

    private static void deleteRoom(Connection connection, int roomId) throws SQLException {
        String deleteBookingsQuery = "DELETE FROM bookings WHERE room_id = ?";
        try (PreparedStatement deleteBookingsStmt = connection.prepareStatement(deleteBookingsQuery)) {
            deleteBookingsStmt.setInt(1, roomId);
            deleteBookingsStmt.executeUpdate();
        }

        String deleteRoomQuery = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement deleteRoomStmt = connection.prepareStatement(deleteRoomQuery)) {
            deleteRoomStmt.setInt(1, roomId);
            deleteRoomStmt.executeUpdate();
            System.out.println("Room and associated bookings deleted successfully!");
        }
    }
}
