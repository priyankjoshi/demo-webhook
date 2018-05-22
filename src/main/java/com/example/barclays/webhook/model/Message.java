package com.example.barclays.webhook.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message {

	
	static final long MESSAGE_TIMEOUT = 24*60*60*1000;
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String messageBody;
	
	@Column(nullable = false)
	private String contentType;
	
	@Column(nullable = false)
	private Timestamp timestamp;
	
	@ManyToOne(optional = false)
	private Destination destinations;
	
	public Message(String messageBody,String contentType, Destination destination) {
		super();
		this.messageBody = messageBody;
		this.contentType = contentType;
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.destinations = destination;
	}
	
	protected Message() {		
	}

	public Long getId() {
		return id;
	}

	

	public String getMessageBody() {
		return messageBody;
	}

	

	public String getContentType() {
		return contentType;
	}

	

	public Timestamp getTimestamp() {
		return timestamp;
	}

	
	public Destination getDestination() {
		return destinations;
	}

	public Long getDestinationId() {
		return destinations.getId();
	}
	
	public String getDestinationUrl() {
		return destinations.getUrl();
	}
	
	public Boolean isMessageTimeout() {
			return timestamp.getTime() < System.currentTimeMillis() - MESSAGE_TIMEOUT;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj ==null)
				return false;
			if(getClass() !=obj.getClass())
				return false;
			Message message = (Message) obj;
			if(id == null) {
				if(message.id !=null)
					return false;
							
			}else {
				if(!id.equals(message.id))
					return false;
			}
				return true;
	}
	@Override
    public String toString() {
        return String.format("Message[id=%d, messageBody='%s', contentType='%s']", id, messageBody, contentType);
}
	
}
