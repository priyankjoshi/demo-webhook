package com.example.barclays.webhook.restcontroller;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.barclays.webhook.events.MessageRecievedEvent;
import com.example.barclays.webhook.model.Destination;
import com.example.barclays.webhook.model.Message;
import com.example.barclays.webhook.persistent.DestinationRepository;
import com.example.barclays.webhook.persistent.MessageRepository;

@RestController
public class DestinationService implements ApplicationEventPublisherAware {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DestinationService.class);

	@Autowired
	private DestinationRepository destinationRepository;

	@Autowired
	private MessageRepository messageRepository;

	// Event Publisher
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Register a new destination (URL) returning its id
	 */
	@RequestMapping(value= "/destinations", method = RequestMethod.POST)
	public Long registerNewDestination(@RequestParam("url") String url) {
		validateParam(url, url);

		Destination destination = destinationRepository.save(new Destination(url));

		logger.debug("Recieved the Destination url {}", url);

		return destination.getId();
	}

	/**
	 * List registered destinations [{id, URL},...]
	 */

	@RequestMapping(value = "/destinations",method = RequestMethod.GET)
	public Iterable<Destination> listDestination() {

		logger.debug("Listing Destinations...");
		return destinationRepository.findAll();
	}

	/**
	 * Delete a destination by id
	 */
	@DeleteMapping("/destinations/{id}")
	public void deleteDestination(@PathVariable("id") Long id) {
		Destination destination = getDestination(id);

		destinationRepository.deleteById(id);

		logger.debug("Deleted Destination {}", destination.getUrl());
	}
	
	
	
	/**
	 * POST a message to this destination
	 */
	@RequestMapping(value ="/destinations/{id}/message", method=RequestMethod.POST)
	public void postMessageToDestination(@PathVariable("id") Long id,
										 @RequestBody String body,
										 @RequestHeader("Content-Type") String contentType) {
		validateParam(body, "body");
		Destination destination = getDestination(id);
		
		Message message = messageRepository.save(new Message(body,contentType,destination));
		
		logger.debug("Received Message {} for Destination {}", message.getId(), message.getDestinationUrl());
		
		applicationEventPublisher.publishEvent(new MessageRecievedEvent(this,message));
		
	}

	private Destination getDestination(Long id) {
		Optional<Destination> destination = destinationRepository.findById(id);
		if (destination.isPresent())
			return destination.get();
		else
			throw new NoSuchElementException("Destination does not exist with ID" + id);
		
	}

	private void validateParam(String param, String paramName) {

		if (param == null || param.isEmpty()) {
			throw new IllegalArgumentException("The " + paramName + " must not be empty or null");
		}
	}

}
