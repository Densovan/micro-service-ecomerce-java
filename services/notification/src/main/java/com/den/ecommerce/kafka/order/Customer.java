package com.den.ecommerce.kafka.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Customer {
    String id;
    String firstname;
    String lastname;
    String email;
}
