package Memenergy.data.composedId;

import Memenergy.data.Comment;
import Memenergy.data.User;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class CommentReportId implements Serializable,Cloneable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (username_username) references user(username) on delete cascade on update cascade"))
    private User username;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false ,foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (id_id) references comment(id) on delete cascade on update cascade"))
    private Comment id;

    public CommentReportId() {
    }

    public CommentReportId(User username, Comment id) {
        this.username = username;
        this.id = id;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public Comment getId() {
        return id;
    }

    public void setId(Comment id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentReportId that = (CommentReportId) o;
        return id == that.id && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id);
    }

    
    @Override
    public CommentReportId clone(){
        CommentReportId aux;
        try {
             aux = (CommentReportId) super.clone();
        }catch (CloneNotSupportedException e) {
            aux = new CommentReportId();

            aux.setId(this.getId());
            aux.setUsername(this.getUsername());
        }
        return aux;
    }
}
