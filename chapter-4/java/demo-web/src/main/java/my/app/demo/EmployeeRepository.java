package my.app.demo;

import com.redis.om.spring.repository.RedisDocumentRepository;

import java.util.List;

public interface EmployeeRepository extends RedisDocumentRepository<Employee,String> {
    List<Employee> findByLastNameAndFirstName(String lastName, String firstName);

}
