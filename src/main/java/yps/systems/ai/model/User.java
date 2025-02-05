package yps.systems.ai.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class User {

    @Id
    @GeneratedValue
    private String elementId;

    @Property("username")
    private String username;

    @Property("password")
    private String password;

}
