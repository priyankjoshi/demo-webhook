package com.example.barclays.webhook.listener;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.barclays.webhook.events.MessageRecievedEvent;
import com.example.barclays.webhook.exception.MessageProcessException;
import com.example.barclays.webhook.model.Destination;
import com.example.barclays.webhook.model.Message;
import com.example.barclays.webhook.persistent.DestinationRepository;
import com.example.barclays.webhook.persistent.MessageRepository;

@Service
public class MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	private final RestTemplate restTemplate;
	
	
	public MessageListener(RestTemplateBuilder resttemplateBuilder) {
		this.restTemplate = resttemplateBuilder.build();
	}
	
	@Async
	@EventListener
	public void messageRecievedListener(MessageRecievedEvent messageRecievedListener) {
		Message message = messageRecievedListener.getMessage();
		logger.debug("Listener Event for the message {}",message.getId());
		
		processMessageForDestination(message.getDestination());
		
		
	}
	
	/**
	 * 
   // 0 0 * * * *" = the top of every hour of every day.
   // 10 * * * * *" = every ten seconds.
   // 0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
   // 0 0 6,19 * * *" = 6:00 AM and 7:00 PM every day.
   // 0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
   // 0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
   // 0 0 0 25 12 ?" = every Christmas Day at midnight

	 * 
	 * 
	 * */
	
	//@Scheduled(cron="*/10 * * * * *")
	/*public void scheduleMessageProcessor() {
		
		logger.debug("Executing scheduled message processor at {}", new Date(System.currentTimeMillis()));
		
		destinationRepository.findAll().forEach(destination -> processMessageForDestination(destination));
		
	}*/

	private void processMessageForDestination(Destination destination) {

		logger.debug("Processing messages for Destination {}", destination.getUrl());
		
		destinationRepository.setDestinationOnline(destination.getId());
		
		List<Message> messages = messageRepository.findAllByDestinationsOrderByIdAsc(destination);
		
		for(Message m:messages) {
			if(m.isMessageTimeout()) {
				deleteMessage(m);
			}
			else {
				try {
					sendMessage(m);
				} catch (MessageProcessException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private void sendMessage(Message msg) throws MessageProcessException {
		
		try {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, msg.getContentType());
		HttpEntity<String> request = new HttpEntity<>(msg.getMessageBody(),headers);
		
		Thread.sleep(500); //wait for 5 sec before message send
		
		logger.debug("Sending message {} to destination url {} ",msg.getId(),msg.getDestinationUrl());
		
		ResponseEntity<String> entity  = restTemplate.postForEntity(msg.getDestinationUrl(), request, String.class);
		
		if(entity.getStatusCode().equals(HttpStatus.OK)) {
			onSendMessageSuccess(msg);
		}else {
			throw new MessageProcessException("Non 200 HttpResponseCode!");
		}
		}catch(Exception ex) {
			logger.info("sendMessage caught an exception: {}", ex.getMessage());
			
			onSendMessageError(msg);
			throw new MessageProcessException(ex.getMessage());
		}
	}
	
	private void onSendMessageError(Message message) {
		logger.debug("Unsent Message {}", message.getId());
		
		destinationRepository.setDestinationOffline(message.getDestinationId());
	}

	private void onSendMessageSuccess(Message msg) {
		logger.debug("Sent Message {}", msg.getId());
		
		deleteMessage(msg);
	}

	private void deleteMessage(Message msg) {
		messageRepository.deleteById(msg.getId());
		
		logger.debug("Deleted Message {}", msg.getId());
	}
	
}
