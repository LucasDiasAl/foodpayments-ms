package com.dias.payments.service;

import com.dias.payments.dto.PaymentDTO;
import com.dias.payments.httpClient.OrderClient;
import com.dias.payments.model.Payment;
import com.dias.payments.model.Status;
import com.dias.payments.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final ModelMapper modelMapper;
    private final OrderClient orderClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepo, ModelMapper modelMapper, OrderClient orderClient) {
        this.paymentRepo = paymentRepo;
        this.modelMapper = modelMapper;
        this.orderClient = orderClient;
    }

    public Page<PaymentDTO> getAll(Pageable page) {
        return paymentRepo.findAll(page).map(p -> modelMapper.map(p, PaymentDTO.class));
    }

    public PaymentDTO getById(Long id) {
        Payment payment = paymentRepo.findById(id).orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO save(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setStatus(Status.CREATED);
        paymentRepo.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO update(Long id, PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setId(id);
        paymentRepo.save(payment);
        return modelMapper.map(payment, PaymentDTO.class);
    }

    public void delete(Long id) {
        paymentRepo.deleteById(id);
    }

    public void confirmPayment(Long id) {
        Optional<Payment> payment = paymentRepo.findById(id);

        if(payment.isEmpty()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.APPROVED);
        try {
        paymentRepo.save(payment.get());
        feign.Response response = orderClient.updatePayment(payment.get().getOrderId());
        if (response.status() == 404) {
            throw new RuntimeException("Service offline");
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void paymentConfirmedWithouIntegration(Long id) {
        Optional<Payment> payment = paymentRepo.findById(id);

        if(payment.isEmpty()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.APPROVED_NO_INTEGRATION);
        paymentRepo.save(payment.get());
    }
}
