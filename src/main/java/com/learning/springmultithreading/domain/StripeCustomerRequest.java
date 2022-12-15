package com.learning.springmultithreading.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerRequest {
    private String email;
    private String name;
    private String description;
    private String paymentMethod;
}
