package Memenergy.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;


@Entity
public class Post implements Comparable<Post>,Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 2700,nullable = false)
    private Calendario date;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private long likeCounter;
    @Column(nullable = false)
    private long reportCounter;
    @Column(length = 1024)
    private String description;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (user_creator_username) references user(username) on delete set null on update cascade"))
    private User userCreator;
    @Column(nullable = false)
    private int forcedVisibility;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (moderated_by_username) references user(username) on delete set null on update cascade"))
    private User moderatedBy;

    public Post() {
        this.date = new Calendario();
        this.likeCounter = 0;
        this.reportCounter = 0;
        this.description = "  ";
        this.forcedVisibility = 0;
        this.moderatedBy = null;
    }

    public Post(String title, String description, User userCreator) {
        this.date = new Calendario();
        this.title = title;
        this.likeCounter = 0;
        this.reportCounter = 0;
        this.description = description;
        this.userCreator = userCreator;
        this.forcedVisibility = 0;
        this.moderatedBy = null;
    }

    public Post(String title, User userCreator) {
        this.date = new Calendario();
        this.title = title;
        this.likeCounter = 0;
        this.reportCounter = 0;
        this.description = "  ";
        this.userCreator = userCreator;
        this.forcedVisibility = 0;
        this.moderatedBy = null;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty
    public Calendario getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(Calendario date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public long getLikeCounter() {
        return likeCounter;
    }

    @JsonIgnore
    public void setLikeCounter(long likeCounter) {
        this.likeCounter = likeCounter;
    }

    @JsonProperty
    public long getReportCounter() {
        return reportCounter;
    }
    @JsonIgnore
    public void setReportCounter(long reportCounter) {
        this.reportCounter = reportCounter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public User getUserCreator() {
        return userCreator;
    }

    @JsonIgnore
    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    public int getForcedVisibility() {
        return forcedVisibility;
    }

    public void setForcedVisibility(int forcedVisibility) {
        if(forcedVisibility<0){
            this.forcedVisibility = -1;
        } else if (forcedVisibility >0){
            this.forcedVisibility = 1;
        }else{
            this.forcedVisibility = 0;
        }
    }

    @JsonIgnore
    public User getModeratedBy() {
        return moderatedBy;
    }

    @JsonIgnore
    public void setModeratedBy(User moderatedBy) {
        this.moderatedBy = moderatedBy;
    }

    public boolean hasUserCreator(){
        return this.userCreator!=null;
    }

    public boolean notHasUserCreator(){
        return this.userCreator==null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Post o) {
        return -this.date.compareTo(o.date);
    }

    
    @Override
    public Post clone(){
        Post aux;

        try {
            aux = (Post) super.clone();
        } catch (CloneNotSupportedException e){
            aux = new Post();

            aux.setDate(this.getDate());
            aux.setTitle(this.getTitle());
            aux.setLikeCounter(this.getLikeCounter());
            aux.setReportCounter(this.getReportCounter());
            aux.setDescription(this.getDescription());
            aux.setUserCreator(this.getUserCreator());
            aux.setForcedVisibility(this.getForcedVisibility());
            aux.setModeratedBy(this.getModeratedBy());
            aux.setId(this.getId());
        }

        return aux;
    }
}
