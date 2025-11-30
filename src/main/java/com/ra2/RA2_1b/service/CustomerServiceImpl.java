package com.ra2.RA2_1b.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.RA2_1b.model.Customer;
import com.ra2.RA2_1b.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public abstract class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initializeDatabase() {
        repository.createTableCustomers();
        repository.insertSampleData();
    }

    @Override
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = repository.findAll();

            if (customers == null || customers.isEmpty()) {
                // Si no hay registros, devuelve null
                return ResponseEntity.ok(null);
            }
            // Devuelve la lista de customers OK
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            // Captura cualquier error y lo devuelve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al recuperar els usuaris: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getCustomerById(long id) {
        try {
            Customer customer = repository.findById(id);
            if (customer == null) {
                // Si no existe, devuelve error
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer no trobat");
            }
            // Devuelve el customer encontrado
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            // Captura errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al recuperar el customer amb id " + id + ": " + e.getMessage());
        }
    }

    @Override
    public void createCustomer(Customer ncustomer, int count) {

        // VALIDACION DE NOMBRE
        if (ncustomer.getName() == null || ncustomer.getName().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nom ha de tenir un mínim de 3 caràcters"
            );
        }
        //VALIDACIÓN DE EDAD
        if (ncustomer.getAge() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La edat ha de ser major que 0"
            );
        }

        for (int i = 1; i <= count; i++) {
            Customer customer = new Customer();
            customer.setName(customer.getName() + i);
            customer.setDescription(customer.getDescription());
            customer.setAge(customer.getAge());
            customer.setCourse(customer.getCourse());
            customer.setPassword(customer.getPassword());
            customer.setDataCreated(LocalDateTime.now());
            customer.setDataUpdated(LocalDateTime.now());

            repository.save(customer);
        }
    }
    @Override
    public ResponseEntity<?> updateCustomer(long id, Customer updatedCustomer) {
            try {
                // Busca el customer existente
                Customer existing = repository.findById(id);
                if (existing == null) {
                    return ResponseEntity.ok(null);
                }
                // Actualiza todos los campos
                existing.setName(updatedCustomer.getName());
                existing.setDescription(updatedCustomer.getDescription());
                existing.setAge(updatedCustomer.getAge());
                existing.setCourse(updatedCustomer.getCourse());
                existing.setPassword(updatedCustomer.getPassword());
                existing.setDataUpdated(LocalDateTime.now()); // Actualiza la fecha de modificación
                // Guarda los cambios
                repository.update(existing);
                return ResponseEntity.ok(existing);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualitzar el customer amb id " + id + ": " + e.getMessage());
            }
    }

    @Override
    public ResponseEntity<Customer> updateCustomerAge(long id, int age) {
        Customer existing = repository.findById(id);

        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Actualiza solo la edad y la fecha de actualización
        existing.setAge(age);
        existing.setDataUpdated(LocalDateTime.now());
        repository.update(existing);

        return ResponseEntity.ok(existing);
    }

    @Override
    public ResponseEntity<String> deleteCustomer(long id) {
        // Elimina el customer por su id
        boolean deleted = repository.deleteById(id);

        if (deleted) {
            return ResponseEntity.ok("Customer amb id " + id + " eliminat correctament.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No s'ha trobat cap customer amb id " + id);
        }
    }
    @Override
    public ResponseEntity<String> uploadCustomerImage(Long userId, MultipartFile imageFile) {
        try {
            //Verificar si el usuario existe
            Customer customer = repository.findById(userId);
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer no trobat amb id " + userId);
            }

            //Crear carpeta si no existe
            Path folderPath = Paths.get("src/main/resources/public/images");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            //Crear nombre único para la imagen
            String originalFilename = imageFile.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "imagen.jpg";
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = "user_" + userId + "_" + System.currentTimeMillis() + extension;
            Path filePath = folderPath.resolve(filename);

            //Guardar la imagen en la carpeta
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //Guardar la ruta en la base de datos (relativa para servirla vía URL)
            String dbPath = "/images/" + filename;
            repository.updateImagePath(userId, dbPath);

            //Devolver URL de la imagen
            return ResponseEntity.ok(dbPath);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperat: " + e.getMessage());
        }
    }
    @Override
    public ResponseEntity<Integer> uploadCsv(MultipartFile csvFile) {
        int addedCount = 0;
        try {
            //Crear carpeta si no existeix
            Path folderPath = Paths.get("src/main/resources/csv_processed");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            //Llegir CSV
            List<String> lines = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))
                    .lines()
                    .toList();

            //Processar cada línia (suposant CSV amb 5 camps: name, description, age, course, password)
            for (String line : lines) {
                String[] parts = line.split(","); // separador coma
                if (parts.length < 5) continue;

                Customer customer = new Customer();
                customer.setName(parts[0].trim());
                customer.setDescription(parts[1].trim());
                customer.setAge(Integer.parseInt(parts[2].trim()));
                customer.setCourse(parts[3].trim());
                customer.setPassword(parts[4].trim());
                customer.setDataCreated(LocalDateTime.now());
                customer.setDataUpdated(LocalDateTime.now());

                repository.save(customer);
                addedCount++;
            }

            //Guardar el fitxer CSV original a la carpeta
            String filename = "csv_" + System.currentTimeMillis() + ".csv";
            Path filePath = folderPath.resolve(filename);
            Files.copy(csvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //Retornar el nombre de registres afegits
            return ResponseEntity.ok(addedCount);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(addedCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(addedCount);
        }
    }
    @Override
    public ResponseEntity<Integer> uploadJson(MultipartFile jsonFile) {
        int addedCount = 0;
        try {
            //Crear carpeta si no existeix
            Path folderPath = Paths.get("src/main/resources/json_processed");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            //Llegir JSON amb Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonFile.getInputStream());
            JsonNode usersNode = root.path("data").path("users");

            if (!usersNode.isArray()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
            }

            //Processar cada usuari
            for (JsonNode userNode : usersNode) {
                Customer customer = new Customer();
                customer.setName(userNode.path("name").asText());
                customer.setDescription(userNode.path("description").asText());
                customer.setCourse(""); // no hi ha camp course en JSON, podem deixar buit
                customer.setPassword(userNode.path("password").asText());
                customer.setDataCreated(LocalDateTime.now());
                customer.setDataUpdated(LocalDateTime.now());

                repository.save(customer);
                addedCount++;
            }

            //Guardar el fitxer JSON original a la carpeta
            String filename = "json_" + System.currentTimeMillis() + ".json";
            Path filePath = folderPath.resolve(filename);
            Files.copy(jsonFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //Retornar el nombre de registres afegits
            return ResponseEntity.ok(addedCount);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(addedCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(addedCount);
        }
    }
}
