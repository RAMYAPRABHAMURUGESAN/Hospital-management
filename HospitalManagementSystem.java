import java.sql.*;
import java.util.*;

public class HospitalManagementSystem {
    static final String URL = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "Ramya@1121"; // change to your MySQL password

    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to hospital_db");

            while (true) {
                System.out.println("\n=== Hospital Management Menu ===");
                System.out.println("1. Add Patient");
                System.out.println("2. Add Doctor");
                System.out.println("3. Add Appointment");
                System.out.println("4. View Patient by ID");
                System.out.println("5. Generate Bill");
                System.out.println("6. Exit");
                System.out.println("7. View All Doctors");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1: addPatient(); break;
                    case 2: addDoctor(); break;
                    case 3: addAppointment(); break;
                    case 4: viewPatientById(); break;
                    case 5: generateBill(); break;
                    case 6: conn.close(); return;
                    case 7: viewAllDoctors(); break;
                    default: System.out.println("Invalid choice.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPatient() throws SQLException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Gender: ");
        String gender = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();
        System.out.print("Address: ");
        String address = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO patients(name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setInt(2, age);
        ps.setString(3, gender);
        ps.setString(4, phone);
        ps.setString(5, address);
        ps.executeUpdate();
        System.out.println("Patient added successfully.");
    }

    public static void addDoctor() throws SQLException {
        System.out.print("Doctor Name: ");
        String name = sc.nextLine();
        System.out.print("Specialty: ");
        String specialty = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO doctors(name, specialty, phone) VALUES (?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, specialty);
        ps.setString(3, phone);
        ps.executeUpdate();
        System.out.println("Doctor added successfully.");
    }

    public static void addAppointment() throws SQLException {
        System.out.print("Enter Patient Name: ");
        String pname = sc.nextLine();

        // Get patient_id from name
        PreparedStatement getPid = conn.prepareStatement("SELECT id FROM patients WHERE name = ?");
        getPid.setString(1, pname);
        ResultSet rs = getPid.executeQuery();

        int pid = -1;
        if (rs.next()) {
            pid = rs.getInt("id");
        } else {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Doctor ID: ");
        int did = sc.nextInt();
        sc.nextLine();
        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Notes: ");
        String notes = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO appointments(patient_id, doctor_id, date, notes) VALUES (?, ?, ?, ?)");
        ps.setInt(1, pid);
        ps.setInt(2, did);
        ps.setString(3, date);
        ps.setString(4, notes);
        ps.executeUpdate();
        System.out.println("Appointment added successfully.");
    }

    public static void viewPatientById() throws SQLException {
        System.out.print("Enter patient ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Name   : " + rs.getString("name"));
            System.out.println("Age    : " + rs.getInt("age"));
            System.out.println("Gender : " + rs.getString("gender"));
            System.out.println("Phone  : " + rs.getString("phone"));
            System.out.println("Address: " + rs.getString("address"));
        } else {
            System.out.println("Patient not found.");
        }
    }

    public static void generateBill() throws SQLException {
        System.out.print("Enter patient ID: ");
        int pid = sc.nextInt();
        System.out.print("Enter bill amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO bills(patient_id, amount, status) VALUES (?, ?, 'Unpaid')");
        ps.setInt(1, pid);
        ps.setDouble(2, amount);
        ps.executeUpdate();
        System.out.println("Bill generated successfully.");
    }

    public static void viewAllDoctors() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM doctors");

        System.out.println("\n--- Doctors List ---");
        while (rs.next()) {
            System.out.println("ID       : " + rs.getInt("id"));
            System.out.println("Name     : " + rs.getString("name"));
            System.out.println("Specialty: " + rs.getString("specialty"));
            System.out.println("Phone    : " + rs.getString("phone"));
            System.out.println("---------------------------");
        }
    }
}
