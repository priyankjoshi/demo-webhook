package com.example.barclays.demowebhook;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories("com.example.barclays.webhook.persistent")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@Configuration
@EnableWebMvc
@ComponentScan(basePackages= {"com.example.barclays.webhook.*"})
@EntityScan("com.example.barclays.webhook.*")
public class DemoWebhookApplication{

	public static void main(String[] args) {
		SpringApplication.run(DemoWebhookApplication.class, args);
	}
	
	/*@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MsgReqService-");
        executor.initialize();
        
        return executor;
	}*/
}
