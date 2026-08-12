package com.den.ecommerce.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.den.ecommerce.email.EmailService;
import com.den.ecommerce.kafka.payment.PaymentConfirmation;
import com.den.ecommerce.kafka.order.OrderConfirmation;
import com.den.ecommerce.notification.Notification;
import com.den.ecommerce.notification.NotificationRepository;
import com.den.ecommerce.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-topic", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentSuccessNotification(ConsumerRecord<String, Object> record) throws MessagingException {
        var paymentConfirmation = objectMapper.convertValue(record.value(), PaymentConfirmation.class);
        log.info(String.format("Consuming the message from payment-topic:: %s", paymentConfirmation));
        var notification = notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );
        log.info("Payment notification saved with id: {}", notification.getId());

        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic", containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderConfirmationNotification(ConsumerRecord<String, Object> record) throws MessagingException {
        var orderConfirmation = objectMapper.convertValue(record.value(), OrderConfirmation.class);
        log.info(String.format("Consuming the message from order-topic:: %s", orderConfirmation));
        var notification = notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );
        log.info("Order notification saved with id: {}", notification.getId());
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}
