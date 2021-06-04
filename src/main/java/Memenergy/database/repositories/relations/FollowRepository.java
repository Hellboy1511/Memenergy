package Memenergy.database.repositories.relations;

import Memenergy.data.composedId.FollowId;
import Memenergy.data.relations.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
}
