package Memenergy.database.repositories.relations;

import Memenergy.data.composedId.LikesId;
import Memenergy.data.relations.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, LikesId> {
}
