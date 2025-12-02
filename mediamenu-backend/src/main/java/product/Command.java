package product;

import org.springframework.http.ResponseEntity;

public interface Command<I,O> {
    ResponseEntity<O> execute (I input);
}
//used to initiate actions, change data (read update delete) typically return success or failure
