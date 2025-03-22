package com.dias.payments.controller;

import com.dias.payments.circuitBreaker.CircuitBreakerCustom;
import com.dias.payments.dto.PaymentDTO;
import com.dias.payments.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;
    private final CircuitBreakerCustom circuitBreakerCustom;

    public PaymentController(PaymentService paymentService, CircuitBreakerCustom circuitBreakerCustom) {
        this.service = paymentService;
        this.circuitBreakerCustom = circuitBreakerCustom;
        this.circuitBreakerCustom.circuitBreakerService();
    }

    @GetMapping
    public Page<PaymentDTO> list(@PageableDefault() Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> findById(@PathVariable @NotNull Long id) {
        PaymentDTO dto = service.getById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> create(@RequestBody @Valid PaymentDTO dto, UriComponentsBuilder ucBuilder) {
        PaymentDTO payment = service.save(dto);
        URI url = ucBuilder.path("/payments/{id}").buildAndExpand(payment.getId()).toUri();
        return ResponseEntity.created(url).body(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> update(@RequestBody @Valid PaymentDTO dto, @PathVariable @NotNull Long id) {
        PaymentDTO payment = service.update(id, dto);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentDTO> delete(@PathVariable @NotNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public void confirmPayment(@PathVariable @NotNull Long id) {
        circuitBreakerCustom.executeWithCircuitBreaker(
                () -> {
                    service.confirmPayment(id);
                    return null;
                },
                () -> {
                    service.paymentConfirmedWithouIntegration(id);
                    return null;
                }
        );
    }
}
