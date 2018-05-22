package com.example.barclays.webhook.persistent;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.barclays.webhook.model.Destination;

public interface DestinationRepository extends CrudRepository<Destination,Long>{

	@Modifying
	@Transactional
	@Query("update Destination d set d.online = true where d.id = :id")
	int setDestinationOnline(@Param("id") Long id);
	
	@Modifying
	@Transactional
	@Query("update Destination d set d.online = false where d.id = :id")
	int setDestinationOffline(@Param("id") Long id);

}
