package com.its.rabbitmqconsumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Slf4j
@EnableBinding(Sink.class)
@SpringBootApplication
@EnableReactiveMongoRepositories
public class RabbitmqConsumerApplication {

	/*@Autowired
	private ReservationRepository reservationRepository;*/

	//@Autowired
	private final ReactiveReservationRepository reservationRepository;

	public RabbitmqConsumerApplication(ReactiveReservationRepository reservationRepository) {
		log.info("Entering and leaving constructor of RabbitmqConsumerApplication after setting" +
				"repository");
		this.reservationRepository = reservationRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqConsumerApplication.class, args);
	}

	@StreamListener(target = Sink.INPUT)
	public void saveReservation(Reservation aReservation) {
		log.info("Entering RabbitmqConsumerApplication : saveReservation with reservation object {} &&& ", aReservation);
		Mono<Reservation> monoReservation = this.reservationRepository.insert(aReservation);
		reservationRepository
			.save(aReservation)
			.subscribe();
		log.info("Reservation {} saved in db and now leaving"); //, savedReservation);
	}
}

@Repository
interface ReactiveReservationRepository extends ReactiveMongoRepository<Reservation, String> {

}

@Repository
interface ReservationRepository extends MongoRepository<Reservation, String> {

}

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Document(collection = "reservation_db")
@JsonIgnoreProperties(ignoreUnknown = true)
class Reservation implements Serializable {
	@NonNull
	@Id
	private String id;

	@NonNull
	@TextIndexed
	private String firstName;

	@NonNull
	@TextIndexed
	private String lastName;

	public Reservation(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
}

//@Configuration
/*@EnableReactiveMongoRepositories
class MongoConfig extends AbstractReactiveMongoConfiguration {

	@Override
	public MongoClient reactiveMongoClient() {
		return MongoClients.create();
	}

	@Override
	protected String getDatabaseName() {
		return "test";
	}

	@Bean
	public ReactiveMongoTemplate reactiveMongoTemplate() {
		return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
	}
}*/
