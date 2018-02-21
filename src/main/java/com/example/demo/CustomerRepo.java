package com.example.demo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Component
public class CustomerRepo {
	static {
        ObjectifyService.register(Customer.class);
    }
	public void saveCustomer(Customer customer) {
		ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ofy().save().entity(customer).now();
            }
        });
	}
	
	public Optional<Customer> fetchCustomer(String id) {
		return ObjectifyService.run(() -> Optional.ofNullable(ofy().load().key(Key.create(Customer.class, id)).now()));
	}

    public List<Customer> allCustomers() {
        return ObjectifyService.run(() -> ofy().load().type(Customer.class).list());
    }

}