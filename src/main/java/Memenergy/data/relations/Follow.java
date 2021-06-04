package Memenergy.data.relations;

import Memenergy.data.User;
import Memenergy.data.composedId.FollowId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class Follow implements Comparable<Follow>,Cloneable {

    @EmbeddedId
    private FollowId followId;

    public Follow() {
        this.followId = new FollowId();
    }

    public Follow(User usernameFollower, User usernameFollowed) {
        this.followId= new FollowId(usernameFollowed,usernameFollower);
    }

    public Follow(FollowId followId) {
        this.followId = followId;
    }

    @JsonProperty
    public User getUsernameFollower() {
        return this.followId.getUsernameFollower();
    }

    @JsonIgnore
    public void setUsernameFollower(User usernameFollower) {
        this.followId.setUsernameFollower(usernameFollower);
    }

    @JsonProperty
    public User getUsernameFollowed() {
        return this.followId.getUsernameFollowed();
    }

    @JsonIgnore
    public void setUsernameFollowed(User usernameFollowed) {
        this.followId.setUsernameFollowed(usernameFollowed);
    }

    public FollowId getFollowId() {
        return followId;
    }

    @JsonIgnore
    public void setFollowId(FollowId followId) {
        this.followId = followId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return followId.equals(follow.followId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followId);
    }

    @Override
    public int compareTo(Follow o) {
        if (o.getUsernameFollower().equals(this.getUsernameFollower())) {
            return this.getUsernameFollowed().compareTo(o.getUsernameFollowed());
        } else {
            return this.getUsernameFollower().compareTo(o.getUsernameFollower());
        }
    }

    
    @Override
    public Follow clone(){
        Follow aux;

        try {
            aux = (Follow) super.clone();
        } catch (CloneNotSupportedException e) {
            aux = new Follow();

            aux.setUsernameFollowed(this.getUsernameFollowed());
            aux.setUsernameFollower(this.getUsernameFollower());
        }

        return aux;
    }
}
