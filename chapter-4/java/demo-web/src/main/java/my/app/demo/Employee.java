package my.app.demo;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Set;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Document
public class Employee {

    @Id
    @Indexed
    private String id;

    @Indexed @NonNull
    private String firstName;

    @Indexed @NonNull
    private String lastName;

    @Indexed @NonNull
    private Integer age;

    @Searchable @NonNull
    private String funFact;

    @Indexed @NonNull
    private Office office;

    @Indexed @NonNull
    private Set<String> roles;
}
