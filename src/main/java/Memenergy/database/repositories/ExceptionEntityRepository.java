package Memenergy.database.repositories;

import Memenergy.data.ExceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionEntityRepository extends JpaRepository<ExceptionEntity, Long> {
}
