package com.example.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.*;

 

@Cache
@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanRequest {

    @Id
    private Long id;

    private double amount;

    private String purpose;

    @Index
    private String customerId;

    private String name;

    private String bsn;

    private double salary;

    public LoanRequest setCustomer(Customer customer) {
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.bsn = customer.getBsn();
        this.salary = customer.getSalary();
        return this;
    }
}