package com.ra2.RA2_1b.model;

import java.time.LocalDateTime;

public class Customer {
    private long id;
    private String name, description, course, password, image_path;
    private int age;
    private LocalDateTime dataCreated;
    private LocalDateTime dataUpdated;

    // Constructor generico obligatorio
    public Customer() {
    }

    // Constructor completo para inicializar todos los campos
    public Customer(long id, String name, String description, int age, String course, String password, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        this.course = course;
        this.password = password;
        this.image_path = imagePath;
        this.dataCreated = LocalDateTime.now(); // Fecha de creación
        this.dataUpdated = LocalDateTime.now(); // Fecha de actualización
    }

    @Override
    public String toString() {
        // Devuelve el customer
        return String.format(
                "Customer[id=%d, name='%s', description='%s', age=%d, course='%s', password='%s', created=%s, updated=%s, image_path='%s]",
                id, name, description, age, course, password, dataCreated, dataUpdated, image_path
        );
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getDataCreated() { return dataCreated; }
    public void setDataCreated(LocalDateTime dataCreated) { this.dataCreated = dataCreated; }

    public LocalDateTime getDataUpdated() { return dataUpdated; }
    public void setDataUpdated(LocalDateTime dataUpdated) { this.dataUpdated = dataUpdated; }

    public String getImagePath() { return image_path; }
    public void setImagePath(String imagePath) { this.image_path = imagePath; }
}
