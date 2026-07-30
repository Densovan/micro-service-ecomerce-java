package com.den.ecommerce.kafka;

import com.den.ecommerce.customer.CustomerResponse;
import com.den.ecommerce.order.PaymentMethod;
import com.den.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
