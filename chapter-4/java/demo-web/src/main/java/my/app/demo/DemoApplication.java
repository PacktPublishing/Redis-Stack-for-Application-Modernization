package my.app.demo;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.*;

@EnableRedisDocumentRepositories
@SpringBootApplication
public class DemoApplication {

	private static Logger log = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	EmployeeRepository employeeRepository;

	@Bean @Order(1)
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			log.debug("Spring Boot beans:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				log.debug("Bean name: {}", beanName);
			}
		};
	}

	@Bean @Order(2)
	CommandLineRunner loadTestData() {
		return args -> {
			employeeRepository.deleteAll();
			Office romeOffice = Office.of("Via Roma", "1", "Roma", "Roma", "00100", "Italy");
			Office macerataOffice = Office.of("Via Macerata", "1", "Macerata", "Macerata", "62100", "Italy");

			Employee luigi = Employee.of("Luigi", "Fugaro", 44, "When I was younger, I spent more than a day without interruption playing Monkey Island. And I couldn't go to school of course.", romeOffice, Set.of("Solution Architect"));
			luigi.setId("000001");
			Employee mirko = Employee.of("Mirko", "Ortensi", 44, "When I was younger, I spent more than a day without interruption playing Pac-Man. And I couldn't go to school of course.", macerataOffice, Set.of("Technical Architect"));
			mirko.setId("000002");
			Employee newLuigi = Employee.of("Luigi", "Fugaro", 44, "When I was younger, I spent more than a day without interruption playing Monkey Island. And I couldn't go to school of course.", romeOffice, Set.of("Senior Solution Architect"));
			newLuigi.setId("000003");

			employeeRepository.save(luigi);
			employeeRepository.save(mirko);
			employeeRepository.save(newLuigi);
		};
	}

	@Bean @Order(3)
	CommandLineRunner findAll() {
		return args -> {
			List<Employee> employees = employeeRepository.findAll();
			employees.forEach(employee -> log.info("Find all Employee: {}", employee));
		};
	}

	@Bean @Order(4)
	CommandLineRunner findById() {
		return args -> {
			Optional<Employee> employee = employeeRepository.findById("000001");
			log.info("Find by ID '000001' Employee: {}", employee.orElseThrow());
		};
	}

	@Bean @Order(5)
	CommandLineRunner findByLastNameAndFirstName() {
		return args -> {
			List<Employee> employees = employeeRepository.findByLastNameAndFirstName("Ortensi", "Mirko");
			employees.forEach(employee -> log.info("Find by firstnale and lastname - Employee: {}", employee));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);


		RSClient client = RSClient.getInstance("redis-16887.c1.us-east1-2.gce.cloud.redislabs.com", 16887, "default", "redis-stack-2023");
		String key = "name";
		String value = "Luigi";
		client.set(key, value);
		String newValue = client.get("name");
		log.info("Retrieved value {} for key {}", newValue, key);

		long numberOfTasks = client.lpush("queue:tasks", "Task-1");
		log.info("numberOfTasks {}", numberOfTasks);
		numberOfTasks = client.lpush("queue:tasks", "Task-2");
		log.info("numberOfTasks {}", numberOfTasks);
		String task = client.rpop("queue:tasks");
		log.info("Retrieved task {}", task);

		client.sadd("employee:ids", "00000001");
		client.sadd("employee:ids", "00000002");
		client.sadd("employee:ids", "00000001");

		Set<String> employeeIds = client.smembers("employee:ids");
		log.info("Employee IDs {}", employeeIds);
		employeeIds.forEach(s -> log.info("Employee: {}", s));
		boolean exists = client.sismember("employee:ids", "00000001");
		log.info("Employee id \"00000001\" exists {}", exists);

		Map<String, Double> scores = new HashMap<>();
		scores.put("PlayerOne", 22.0);
		scores.put("PlayerTwo", 10.0);
		scores.put("PlayerThree", 78.0);
		log.info("Scores map {}", scores);
		Long addedScores = client.zadd("players", scores);
		log.info("Added scores {}", addedScores);
		List<String> players = client.zrangeByScore("players", 0, 50);
		log.info("Players with score in range 0-50 {}", players);
		Double score = client.zscore("players", "PlayerTwo");
		log.info("Score for \"PlayerTwo\" {}", score);


		Map<String,String> user = new HashMap<>();
		user.put("firstname", "Luigi");
		user.put("lastname", "Fugaro");
		user.put("username", "foogaro");
		log.info("User {}", user);

		Long numberOfFields = client.hset("user:1", user);
		log.info("numberOfFields {}", numberOfFields);
		numberOfFields = client.hset("user:1", "email", "luigi@foogaro.com");
		log.info("numberOfFields {}", numberOfFields);
		String firstname = client.hget("user:1", "firstname");
		log.info("User firstname {}", firstname);
		Map<String, String> fields = client.hgetAll("user:1");
		log.info("All fields {}", fields);

	}

}
