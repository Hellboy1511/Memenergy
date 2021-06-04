package Memenergy.data.relations;

import Memenergy.data.Calendario;
import Memenergy.data.Comment;
import Memenergy.data.User;
import Memenergy.data.composedId.CommentReportId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class CommentReport implements Comparable<CommentReport>,Cloneable {

    @EmbeddedId
    private CommentReportId commentReportId;
    @Column(nullable = false,length = 1024)
    private String reason;
    @Column(length = 2700,nullable = false)
    private Calendario date;

    public CommentReport() {
        this.commentReportId = new CommentReportId();
        this.date = new Calendario();
    }

    public CommentReport(User username, Comment id, String reason) {
        this.commentReportId = new CommentReportId(username,id);
        this.reason = reason;
        this.date = new Calendario();
    }

    public CommentReport(CommentReportId commentReportId, String reason) {
        this.commentReportId = commentReportId;
        this.reason = reason;
        this.date = new Calendario();
    }

    @JsonProperty
    public User getUsername() {
        return this.commentReportId.getUsername();
    }

    @JsonIgnore
    public void setUsername(User username) {
        this.commentReportId.setUsername(username);
    }

    @JsonProperty
    public Comment getId() {
        return this.commentReportId.getId();
    }

    @JsonIgnore
    public void setId(Comment id) {
        this.commentReportId.setId(id);
    }

    public CommentReportId getCommentReportId() {
        return commentReportId;
    }

    @JsonIgnore
    public void setCommentReportId(CommentReportId commentReportId) {
        this.commentReportId = commentReportId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty
    public Calendario getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(Calendario date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentReport that = (CommentReport) o;
        return commentReportId.equals(that.commentReportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentReportId);
    }

    @Override
    public int compareTo(CommentReport o) {
        return -this.date.compareTo(o.date);
    }

    
    @Override
    public CommentReport clone(){
        CommentReport aux;

        try {
            aux = (CommentReport) super.clone();
        }catch (CloneNotSupportedException e) {
            aux = new CommentReport();

            aux.setUsername(this.getUsername());
            aux.setId(this.getId());
            aux.setReason(this.getReason());
            aux.setDate(this.getDate());
        }

        return aux;
    }
}
