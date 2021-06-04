package Memenergy.database.repositories.relations;

import Memenergy.data.composedId.CommentReportId;
import Memenergy.data.relations.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, CommentReportId> {
}
