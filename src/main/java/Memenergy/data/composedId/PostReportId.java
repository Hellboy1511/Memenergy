package Memenergy.data.composedId;

import Memenergy.data.Post;
import Memenergy.data.User;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class PostReportId implements Serializable,Cloneable {
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false ,foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (username_username) references user(username) on delete cascade on update cascade"))
    private User username;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false ,foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (id_id) references post(id) on delete cascade on update cascade"))
    private Post id;

    public PostReportId() {
    }

    public PostReportId(User username, Post id) {
        this.username = username;
        this.id = id;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public Post getId() {
        return id;
    }

    public void setId(Post id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostReportId that = (PostReportId) o;
        return id == that.id && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id);
    }

    
    @Override
    public PostReportId clone(){
        PostReportId aux;

        try {
            aux = (PostReportId) super.clone();
        } catch (CloneNotSupportedException e) {
            aux = new PostReportId();

            aux.setId(this.getId());
            aux.setUsername(this.getUsername());
        }

        return aux;
    }
}
