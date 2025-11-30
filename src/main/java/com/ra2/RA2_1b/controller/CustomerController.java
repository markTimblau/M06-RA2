package com.ra2.RA2_1b.controller;

import com.ra2.RA2_1b.model.Customer;
import com.ra2.RA2_1b.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService service;
    //CONTROLADOR NECESARIO
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @RequestMapping("/hello")
    public String jdbctemp() {
        // endpoint que devuelve un texto
        return "hello";
    }

    @PostMapping("/init-db")
    public String initializeDatabase() {
        // Crea la tabla de customers y agrega datos de ejemplo
        service.initializeDatabase();
        return "Base de dades inicialitzada correctament";
    }

    @PostMapping("/customer")
    public ResponseEntity<String> createCustomers(@RequestBody Customer customer) {
        // Crea 10 Customers
        service.createCustomer(customer,10);
        return ResponseEntity.status(HttpStatus.CREATED).body("S'han inserit correctament 10 alumnes!");
    }

    @GetMapping("/customer")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(service.getAllCustomers());
    }

    @GetMapping("/customer/{customer_id}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customer_id") long id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }

    @PutMapping("/customer/{customer_id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable("customer_id") long id,
            @RequestBody Customer updatedCustomer) {
            return ResponseEntity.ok(service.updateCustomer(id, updatedCustomer));
    }

    @PatchMapping("/customer/{customer_id}/age")
    public ResponseEntity<Customer> updateCustomerAge(
            @PathVariable("customer_id") long customerId,
            @RequestParam("age") int age) {
            return service.updateCustomerAge(customerId, age);
    }

    @DeleteMapping("/customer/{customer_id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("customer_id") long customerId) {
        return service.deleteCustomer(customerId);
    }
    //public ResponseEntity<String> uploadCustomerImage()
    @PostMapping("/upload-csv")
    public ResponseEntity<Integer> uploadCsv(@RequestParam("csvFile") MultipartFile csvFile) {
        return service.uploadCsv(csvFile);
    }
    @PostMapping("/upload-json")
    public ResponseEntity<String> uploadJson(@RequestParam("jsonFile") MultipartFile jsonFile) {

    }
}
