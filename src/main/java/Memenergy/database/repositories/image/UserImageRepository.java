package Memenergy.database.repositories.image;

import Memenergy.data.images.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, String> {
}
