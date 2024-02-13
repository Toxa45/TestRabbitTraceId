package org.example;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ApplicationRunner myApplicationRunner(StreamBridge streamBridge, ObservationRegistry observationRegistry) {
        return args ->
                Observation.createNotStarted("my parent observation", observationRegistry)
                        .observe(() -> {
                            String data = "my data";
                            log.info("Send data to RabbitMQ: " + data);
                            streamBridge.send("test-out-0", data);
                        });
    }

    @Bean
    public Consumer<Message> testListener() {
        return message ->{
            log.info("Received message from RabbitMQ: " + message);
        };
    }
}
