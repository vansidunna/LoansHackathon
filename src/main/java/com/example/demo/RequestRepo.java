package com.example.demo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Component
public class RequestRepo {
	static {
        ObjectifyService.register(Request.class);
    }
	public void save(Request req) {
		ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                ofy().save().entity(req).now();
            }
        });
	}
	
	public Optional<Request> get(Long id) {
		return ObjectifyService.run(() -> Optional.ofNullable(ofy().load().key(Key.create(Request.class, id)).now()));
	}
}