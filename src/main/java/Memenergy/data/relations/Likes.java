package Memenergy.data.relations;

import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.data.composedId.LikesId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class Likes implements Comparable<Likes>,Cloneable {

    @EmbeddedId
    private LikesId likesId;

    public Likes() {
        this.likesId = new LikesId();
    }

    public Likes(User username, Post id) {
        this.likesId = new LikesId(username,id);
    }

    public Likes(LikesId likesId) {
        this.likesId = likesId;
    }

    @JsonProperty
    public User getUsername() {
        return this.likesId.getUsername();
    }

    @JsonIgnore
    public void setUsername(User username) {
        this.likesId.setUsername(username);
    }

    @JsonProperty
    public Post getId() {
        return this.likesId.getId();
    }

    @JsonIgnore
    public void setId(Post id) {
        this.likesId.setId(id);
    }

    public LikesId getLikesId() {
        return likesId;
    }

    @JsonIgnore
    public void setLikesId(LikesId likesId) {
        this.likesId = likesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Likes likes = (Likes) o;
        return likesId.equals(likes.likesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likesId);
    }

    @Override
    public int compareTo(Likes o) {
        if (o.getUsername().equals(this.getUsername())) {
            return (this.getId().compareTo(o.getId())) < 0 ? -1 : 1;
        } else {
            return this.getUsername().compareTo(o.getUsername());
        }
    }

    
    @Override
    public Likes clone(){
        Likes aux;

        try {
            aux = (Likes) super.clone();
        } catch (CloneNotSupportedException e) {
            aux= new Likes();

            aux.setUsername(this.getUsername());
            aux.setId(this.getId());
        }

        return aux;
    }
}
