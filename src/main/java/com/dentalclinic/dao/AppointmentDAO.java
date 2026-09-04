package com.dentalclinic.dao;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "WHERE a.appointment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Appointment getAppointmentByNumber(String appointmentNumber) {
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "WHERE a.appointment_number = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "WHERE a.appointment_date = ? " +
                "ORDER BY a.appointment_time";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "WHERE a.patient_id = ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDentistAndDate(int dentistId, LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name as p_first, p.last_name as p_last, " +
                "d.first_name as d_first, d.last_name as d_last, " +
                "t.treatment_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.patient_id " +
                "LEFT JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "LEFT JOIN treatments t ON a.treatment_id = t.treatment_id " +
                "WHERE a.dentist_id = ? AND a.appointment_date = ? " +
                "AND a.status != 'CANCELLED' " +
                "ORDER BY a.appointment_time";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
            System.out.println("Found " + appointments.size() + " appointments for dentist " + dentistId + " on " + date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public boolean addAppointment(Appointment appointment) {
        // Generate a unique appointment number with retry logic
        String appointmentNumber = generateUniqueAppointmentNumber();
        appointment.setAppointmentNumber(appointmentNumber);

        String sql = "INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, " +
                "appointment_date, appointment_time, duration_minutes, status, notes, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, appointment.getAppointmentNumber());
            stmt.setInt(2, appointment.getPatientId());
            stmt.setInt(3, appointment.getDentistId());
            if (appointment.getTreatmentId() != null) {
                stmt.setInt(4, appointment.getTreatmentId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setDate(5, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(6, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setInt(7, appointment.getDurationMinutes());
            stmt.setString(8, appointment.getStatus());
            stmt.setString(9, appointment.getNotes());
            stmt.setInt(10, appointment.getCreatedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    appointment.setAppointmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id = ?, dentist_id = ?, treatment_id = ?, " +
                "appointment_date = ?, appointment_time = ?, duration_minutes = ?, " +
                "status = ?, notes = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDentistId());
            if (appointment.getTreatmentId() != null) {
                stmt.setInt(3, appointment.getTreatmentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setInt(6, appointment.getDurationMinutes());
            stmt.setString(7, appointment.getStatus());
            stmt.setString(8, appointment.getNotes());
            stmt.setInt(9, appointment.getAppointmentId());

            int rows = stmt.executeUpdate();
            System.out.println("Updated appointment " + appointment.getAppointmentId() + ", rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateUniqueAppointmentNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        int maxAttempts = 100;
        int attempt = 0;

        while (attempt < maxAttempts) {
            attempt++;
            // Get the count of appointments for today
            int count = getTodayAppointmentCount();
            String appointmentNumber = String.format("APP-%s-%03d", date, count + attempt);

            // Check if this number already exists
            if (!appointmentNumberExists(appointmentNumber)) {
                System.out.println("Generated unique appointment number: " + appointmentNumber);
                return appointmentNumber;
            }
        }

        // Fallback: use timestamp
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String appointmentNumber = String.format("APP-%s-%s", date, timestamp);
        System.out.println("Generated fallback appointment number: " + appointmentNumber);
        return appointmentNumber;
    }

    private boolean appointmentNumberExists(String appointmentNumber) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_number = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appointmentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTodayAppointmentCount() {
        String sql = "SELECT COUNT(*) as count FROM appointments WHERE DATE(appointment_date) = CURDATE() AND status != 'CANCELLED'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setAppointmentNumber(rs.getString("appointment_number"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDentistId(rs.getInt("dentist_id"));
        appointment.setTreatmentId(rs.getObject("treatment_id") != null ? rs.getInt("treatment_id") : null);
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setDurationMinutes(rs.getInt("duration_minutes"));
        appointment.setStatus(rs.getString("status"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedBy(rs.getInt("created_by"));

        try {
            appointment.setPatientName(rs.getString("p_first") + " " + rs.getString("p_last"));
            appointment.setDentistName("Dr. " + rs.getString("d_first") + " " + rs.getString("d_last"));
            appointment.setTreatmentName(rs.getString("treatment_name"));
        } catch (SQLException e) {
            // Some fields might be null
        }

        return appointment;
    }
}