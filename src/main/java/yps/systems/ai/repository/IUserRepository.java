package yps.systems.ai.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.User;

@Repository
public interface IUserRepository extends Neo4jRepository<User, String> {

    @Query("MATCH (p:Person), (u:User) " +
            "WHERE elementId(p) = $personElementId " +
            "AND elementId(u) = $userElementId " +
            "CREATE (p)-[:HAS_ACCOUNT]->(u)")
    void setUserRelationTo(String personElementId, String userElementId);

    @Query("MATCH (u:User) " +
            "WHERE elementId(u) = $userElementId " +
            "MATCH (:Person)-[ha:HAS_ACCOUNT]->(u) " +
            "DELETE ha")
    void deleteUserRelation(String userElementId);

}
