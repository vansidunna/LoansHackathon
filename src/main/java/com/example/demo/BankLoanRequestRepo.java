package com.example.demo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Component
public class BankLoanRequestRepo {
    static {
        ObjectifyService.register(BankLoanRequest.class);
    }

    public void save(BankLoanRequest req) {
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ofy().save().entity(req).now();
            }
        });
    }

    public Optional<BankLoanRequest> get(Long id) {
        return ObjectifyService.run(() -> Optional.ofNullable(ofy().load().key(Key.create(BankLoanRequest.class, id)).now()));
    }

    public List<BankLoanRequest> getAll() {
        return ObjectifyService.run(() -> ofy().load().type(BankLoanRequest.class).list());
    }

    public List<BankLoanRequest> fetchByLoanRequestId(long loanRequestId) {
        return ObjectifyService.run(() -> ofy().load().type(BankLoanRequest.class).filter("loanRequestId", loanRequestId).list());
    }

    public List<BankLoanRequest> fetchByBankId(String bankId) {
        return ObjectifyService.run(() -> ofy().load().type(BankLoanRequest.class).filter("bankId", bankId).list());
    }


}