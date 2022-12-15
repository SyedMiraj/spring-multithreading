package com.learning.springmultithreading.controller;

import com.learning.springmultithreading.domain.StripeCustomerRequest;
import com.learning.springmultithreading.service.StripeService;
import com.stripe.model.Customer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/stripe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping(path = "/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody StripeCustomerRequest customerRequest){
       return ResponseEntity.accepted().body(stripeService.createCustomer(customerRequest));
    }

    @GetMapping(path = "/customers")
    public ResponseEntity<List<Customer>> createCustomer(){
        return ResponseEntity.accepted().body(stripeService.getAllCustomers());
    }

}
