package com.example.barclays.webhook.events;

import org.springframework.context.ApplicationEvent;

import com.example.barclays.webhook.model.Message;

public class MessageRecievedEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Message message;

	public MessageRecievedEvent(Object source, Message message) {
		super(source);
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

}
