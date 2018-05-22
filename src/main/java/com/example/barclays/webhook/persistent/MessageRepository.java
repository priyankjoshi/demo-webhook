package com.example.barclays.webhook.persistent;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.barclays.webhook.model.Destination;
import com.example.barclays.webhook.model.Message;

public interface MessageRepository extends CrudRepository<Message,Long> {
	
	List<Message> findAllByDestinationsOrderByIdAsc(Destination destinations);

}
