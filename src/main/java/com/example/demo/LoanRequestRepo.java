package com.example.demo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Component
public class LoanRequestRepo {
	static {
        ObjectifyService.register(LoanRequest.class);
    }
	public void save(LoanRequest req) {
		ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ofy().save().entity(req).now();
            }
        });
	}
	
	public Optional<LoanRequest> get(Long id) {
		return ObjectifyService.run(() -> Optional.ofNullable(ofy().load().key(Key.create(LoanRequest.class, id)).now()));
	}

    public List<LoanRequest> getAll() {
        return ObjectifyService.run(() -> ofy().load().type(LoanRequest.class).list());
    }

}