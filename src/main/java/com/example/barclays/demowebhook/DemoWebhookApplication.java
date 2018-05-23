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
@ComponentScan(basePackages= {"com.example.barclays.webhook.*"})
@EntityScan("com.example.barclays.webhook.*")
public class DemoWebhookApplication extends AsyncConfigurerSupport{

	public static void main(String[] args) {
		SpringApplication.run(DemoWebhookApplication.class, args);
	}
	
	
	/*The @EnableAsync annotation switches on Spring’s ability to run @Async methods in a background thread pool
	 *  This class also customizes the used Executor
	 * */
	
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MsgReqService-");
        executor.initialize();
        
        return executor;
	}
}
