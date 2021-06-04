package Memenergy.database.services.relations;

import Memenergy.data.relations.PostReport;
import Memenergy.database.repositories.relations.PostReportRepository;
import Memenergy.database.services.PostService;
import Memenergy.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostReportService {

    @Autowired
    private PostReportRepository repository;
    @Autowired
    EntityManager entityManager;

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    //get a post report given the username of the reporter and the post's id
    public PostReport get(String username, long id) {
        try {
            return this.entityManager.createQuery("select pr from PostReport pr where pr.postReportId.id.id=:id and pr.postReportId.username.username=:username", PostReport.class).setParameter("id",id).setParameter("username",username).getSingleResult().clone();
        }
        catch(NoResultException e){
            return null;
        }
    }


    //create a new post report
    public PostReport create(PostReport postReport) {
        if (this.get(postReport.getUsername().getUsername(), postReport.getId().getId()) != null) {
            return null;
        } else {
            if (this.postService.get(postReport.getId().getId()) != null) {
                this.postService.report(postReport.getId().getId());
                return repository.saveAndFlush(postReport);
            } else {
                return null;
            }
        }
    }

    //update a post report given the username of the reporter and the post's id
    @Transactional
    public PostReport update(String username, long id, PostReport updatedPostReport) {
        PostReport actualPostReport = this.get(username,id);
        if(actualPostReport!=null){
            boolean change = false;
            boolean reasonChanged = false;
            boolean dateChanged = false;

            StringBuilder sb = new StringBuilder();
            sb.append("update PostReport pr set ");

            if(!(updatedPostReport.getReason()==null || Objects.equals(actualPostReport.getReason(),updatedPostReport.getReason()))){
                change = true;
                sb.append("pr.reason=:reason");
                actualPostReport.setReason(updatedPostReport.getReason());
                reasonChanged = true;
            }
            if (!(updatedPostReport.getDate()==null || Objects.equals(actualPostReport.getDate(),updatedPostReport.getDate()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("pr.date=:date");
                actualPostReport.setDate(updatedPostReport.getDate());
                dateChanged = true;
            }



            if(change){
                sb.append(" where pr.commentReportId.id.id=:id and pr.commentReportId.username.username=:username ");
                Query query = this.entityManager.createQuery(sb.toString()).setParameter("id",id).setParameter("username",username);
                if(reasonChanged){
                    query.setParameter("reason",updatedPostReport.getReason());
                }
                if(dateChanged){
                    query.setParameter("date",updatedPostReport.getDate());
                }

                if(query.executeUpdate()>0){
                    return actualPostReport;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //delete a post report given the username of the reporter and the post's id
    @Transactional
    public PostReport delete(String username, long id) {
        PostReport deletedPostReport = this.get(username, id);
        if (deletedPostReport != null) {
            this.entityManager.createQuery("delete from PostReport pr where pr.postReportId.id.id=:id and pr.postReportId.username.username=:username", PostReport.class).setParameter("id",id).setParameter("username",username).executeUpdate();
            return deletedPostReport;
        } else {
            return null;
        }
    }

    // return 10 CommentReports from a specific post given its id and page, may return a null element or less than 10 post reports
    public Collection<PostReport> byPost(long id, long page) {
        Stream<PostReport> stream;
        List<PostReport> listFinal = new LinkedList<>();
        page--;
        List<PostReport> aux = entityManager.createQuery("select pr from PostReport pr where pr.postReportId.id.id = :id", PostReport.class).setParameter("id",id).getResultList();
        stream = aux.parallelStream().sorted().sequential();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = stream.skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }
    }

    // return 10 CommentReports given page, may return a null element or less than 10 post reports
    public Collection<PostReport> byPage(long page) {
        Stream<PostReport> stream;
        List<PostReport> listFinal = new LinkedList<>();
        page--;
        List<PostReport> aux = entityManager.createQuery("select pr from PostReport pr", PostReport.class).getResultList();
        stream = aux.parallelStream().sorted().sequential();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = stream.skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }
    }
}
