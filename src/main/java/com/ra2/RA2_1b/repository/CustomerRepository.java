package com.ra2.RA2_1b.repository;

import com.ra2.RA2_1b.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomerRepository {

    // Necesario para interactuar con la base de datos
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Convertir filas SQL en objetos Customer
    private final class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            // Mapear los campos de la tabla a los atributos de la clase
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("Name"));
            customer.setDescription(rs.getString("Description"));
            customer.setAge(rs.getInt("Age"));
            customer.setCourse(rs.getString("Course"));
            customer.setPassword(rs.getString("Password"));

            // Convertir timestamps a LocalDateTime
            Timestamp created = rs.getTimestamp("dataCreated");
            if (created != null) {
                customer.setDataCreated(created.toLocalDateTime());
            }
            Timestamp updated = rs.getTimestamp("dataUpdated");
            if (updated != null) {
                customer.setDataUpdated(updated.toLocalDateTime());
            }

            return customer;
        }
    }

    // Crear tabla 'customers' en la base de datos
    public void createTableCustomers() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS customers"); // Eliminar tabla si existe
        jdbcTemplate.execute("""
            CREATE TABLE customers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                Name VARCHAR(255),
                image_path VARCHAR(500) NULL,
                Description VARCHAR(255),
                Age INT,
                Course VARCHAR(255),
                Password VARCHAR(255),
                dataCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                dataUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """); // Crear tabla con campos especificados
    }

    // Insertar datos de ejemplo en la tabla
    public void insertSampleData() {
        jdbcTemplate.update(
                "INSERT INTO customers (Name, image_path, Description, Age, Course, Password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "John", "?","Alumno de prueba", 20, "DAW", "pass123", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now())
        );
        jdbcTemplate.update(
                "INSERT INTO customers (Name, image_path, Description, Age, Course, Password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "Jane","?", "Alumno de ejemplo", 21, "DAM", "pass456", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now())
        );
    }

    // Recuperar todos los customers de la base de datos
    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM customers", new CustomerRowMapper());
    }

    // Guardar un nuevo customer en la base de datos
    public void save(Customer customer) {
        // Asegurarse de que las fechas no sean nulas
        if (customer.getDataCreated() == null) customer.setDataCreated(LocalDateTime.now());
        if (customer.getDataUpdated() == null) customer.setDataUpdated(LocalDateTime.now());

        String sql = "INSERT INTO customers (Name, image_path, Description, Age, Course, Password, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                customer.getName(),
                customer.getImagePath(),
                customer.getDescription(),
                customer.getAge(),
                customer.getCourse(),
                customer.getPassword(),
                Timestamp.valueOf(customer.getDataCreated()),
                Timestamp.valueOf(customer.getDataUpdated())
        );
    }

    // Buscar un customer por su id
    public Customer findById(long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        List<Customer> results = jdbcTemplate.query(sql, ps -> ps.setLong(1, id), new CustomerRowMapper());
        return results.isEmpty() ? null : results.get(0); // Devolver null si no hay resultados
    }

    // Actualizar un customer existente
    public void update(Customer customer) {
        String sql = """
                UPDATE customers
                SET Name = ?, image_path = ?, Description = ?, Age = ?, Course = ?, Password = ?, dataUpdated = ?
                WHERE id = ?
            """;
        jdbcTemplate.update(sql,
                customer.getName(),
                customer.getImagePath(),
                customer.getDescription(),
                customer.getAge(),
                customer.getCourse(),
                customer.getPassword(),
                Timestamp.valueOf(customer.getDataUpdated()),
                customer.getId()
        );
    }

    // Actualizar solo la edad de un customer
    public void updateAge(long id, int age) {
        String sql = "UPDATE customers SET Age = ?, dataUpdated = ? WHERE id = ?";
        jdbcTemplate.update(sql, age, Timestamp.valueOf(LocalDateTime.now()), id);
    }

    // Eliminar un customer por id
    public boolean deleteById(long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0; // Devolver true si se eliminó algún registro
    }
    public void updateImagePath(long id, String imagePath) {
        String sql = "UPDATE customers SET image_path = ?, dataUpdated = ? WHERE id = ?";
        jdbcTemplate.update(sql, imagePath, Timestamp.valueOf(LocalDateTime.now()), id);
    }
}
