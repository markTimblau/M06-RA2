package com.ra2.RA2_1b.service;

import com.ra2.RA2_1b.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

    void initializeDatabase();
    ResponseEntity<?> getAllCustomers();
    ResponseEntity<?> getCustomerById(long id);
    void createCustomer(Customer ncustomer, int count);
    ResponseEntity<?> updateCustomer(long id, Customer updated);
    ResponseEntity<Customer> updateCustomerAge(long customerId, int age);
    ResponseEntity<String> deleteCustomer(long id);
    ResponseEntity<String> uploadCustomerImage(Long userId, MultipartFile imageFile);
    ResponseEntity<Integer> uploadCsv(MultipartFile csvFile);
    ResponseEntity<Integer> uploadJson(MultipartFile jsonFile);
}
