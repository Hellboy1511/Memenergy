package Memenergy.database.repositories.relations;

import Memenergy.data.composedId.PostReportId;
import Memenergy.data.relations.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, PostReportId> {
}
