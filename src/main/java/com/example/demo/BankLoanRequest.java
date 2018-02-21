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
public class BankLoanRequest {

    @Id
    private Long id;

    @Index
    private String bankId;


    private boolean approved;

    private double interestRate;

    private double approvedAmount;

    private String comments;


    @Index
    private Long loanRequestId;

    private double amount;

    private String purpose;



    private String customerId;

    private String name;

    private String bsn;

    private double salary;

    public BankLoanRequest setLoanRequest(LoanRequest loanRequest) {
        this.loanRequestId = loanRequest.getId();
        this.amount = loanRequest.getAmount();
        this.purpose = loanRequest.getPurpose();
        this.customerId = loanRequest.getCustomerId();
        this.name = loanRequest.getName();
        this.bsn = loanRequest.getBsn();
        this.salary = loanRequest.getSalary();

        return this;
    }
}