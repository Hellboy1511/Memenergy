package Memenergy.database.repositories.image;

import Memenergy.data.images.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
