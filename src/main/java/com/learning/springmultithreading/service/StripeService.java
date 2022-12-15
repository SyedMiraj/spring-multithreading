package com.learning.springmultithreading.service;

import com.learning.springmultithreading.domain.StripeCustomerRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    public Customer createCustomer(StripeCustomerRequest customerRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", customerRequest.getEmail());
        params.put("name", customerRequest.getName());
        params.put("description", customerRequest.getDescription());
        RequestOptions requestOptions = getRequestOptions();
        try {
            return Customer.create(params, requestOptions);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RequestOptions getRequestOptions() {
        RequestOptions requestOptions = RequestOptions.builder()
                .setApiKey(stripeSecretKey)
                .setStripeAccount("acct_1MFDSmSFpJnXjekI")
                .build();
        return requestOptions;
    }

    public List<Customer> getAllCustomers() {
        RequestOptions requestOptions = getRequestOptions();
        Map<String, Object> params = new HashMap<>();
        try {
            return Customer.list(params, requestOptions).getData();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
