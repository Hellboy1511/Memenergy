package Memenergy.data.relations;

import Memenergy.data.Calendario;
import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.data.composedId.PostReportId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class PostReport implements Comparable<PostReport>,Cloneable {

    @EmbeddedId
    private PostReportId postReportId;
    @Column(nullable = false,length = 1024)
    private String reason;
    @Column(length = 2700,nullable = false)
    private Calendario date;

    public PostReport() {
        this.postReportId = new PostReportId();
        this.date = new Calendario();
    }

    public PostReport(User username, Post id, String reason) {
        this.postReportId = new PostReportId(username,id);
        this.reason = reason;
        this.date = new Calendario();
    }

    public PostReport(PostReportId postReportId, String reason) {
        this.postReportId = postReportId;
        this.reason = reason;
        this.date = new Calendario();
    }

    @JsonProperty
    public User getUsername() {
        return this.postReportId.getUsername();
    }

    @JsonIgnore
    public void setUsername(User username) {
        this.postReportId.setUsername(username);
    }

    @JsonProperty
    public Post getId() {
        return this.postReportId.getId();
    }

    @JsonIgnore
    public void setId(Post id) {
        this.postReportId.setId(id);
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

    public PostReportId getPostReportId() {
        return postReportId;
    }

    @JsonIgnore
    public void setPostReportId(PostReportId postReportId) {
        this.postReportId = postReportId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostReport that = (PostReport) o;
        return postReportId.equals(that.postReportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postReportId);
    }

    @Override
    public int compareTo(PostReport o) {
        return -this.date.compareTo(o.date);
    }

    
    @Override
    public PostReport clone(){
        PostReport aux;

        try {
            aux = (PostReport) super.clone();
        } catch (CloneNotSupportedException e) {
            aux= new PostReport();

            aux.setUsername(this.getUsername());
            aux.setId(this.getId());
            aux.setReason(this.getReason());
            aux.setDate(this.getDate());
        }

        return aux;
    }
}
