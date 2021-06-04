package Memenergy.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;


@Entity
public class Comment implements Comparable<Comment>,Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,length = 1024)
    private String text;
    @Column(length = 2700,nullable = false)
    private Calendario date;
    @Column(nullable = false)
    private long reportCounter;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (user_creator_username) references user(username) on delete set null on update cascade"))
    private User userCreator;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false,foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (related_post_id) references post(id) on delete cascade on update cascade"))
    private Post relatedPost;
    @Column(nullable = false)
    private int forcedVisibility;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (moderated_by_username) references user(username) on delete set null on update cascade"))
    private User moderatedBy;

    public Comment() {
        this.date = new Calendario();
        this.reportCounter = 0;
        this.forcedVisibility = 0;
        this.moderatedBy = null;
    }

    public Comment(String text, User userCreator, Post relatedPost) {
        this.text = text;
        this.date = new Calendario();
        this.reportCounter = 0;
        this.userCreator = userCreator;
        this.relatedPost = relatedPost;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    public Calendario getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(Calendario date) {
        this.date = date;
    }

    @JsonProperty
    public long getReportCounter() {
        return reportCounter;
    }

    @JsonIgnore
    public void setReportCounter(long reportCounter) {
        this.reportCounter = reportCounter;
    }

    @JsonProperty
    public User getUserCreator() {
        return userCreator;
    }

    @JsonIgnore
    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    @JsonProperty
    public Post getRelatedPost() {
        return relatedPost;
    }

    @JsonIgnore
    public void setRelatedPost(Post relatedPost) {
        this.relatedPost = relatedPost;
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
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Comment o) {
        return -this.date.compareTo(o.date);
    }

    
    @Override
    public Comment clone(){
        Comment aux;

        try {
            aux = (Comment) super.clone();
        } catch (CloneNotSupportedException e) {
            aux = new Comment();

            aux.setUserCreator(this.getUserCreator());
            aux.setRelatedPost(this.getRelatedPost());
            aux.setDate(this.getDate());
            aux.setModeratedBy(this.getModeratedBy());
            aux.setReportCounter(this.getReportCounter());
            aux.setText(this.getText());
            aux.setForcedVisibility(this.getForcedVisibility());
            aux.setId(this.getId());
        }

        return aux;
    }
}
