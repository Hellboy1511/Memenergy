package Memenergy.data.composedId;

import Memenergy.data.User;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class FollowId implements Serializable,Cloneable {
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (username_follower_username) references user(username) on delete cascade on update cascade"))
    private User usernameFollowed;
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (username_followed_username) references user(username) on delete cascade on update cascade"))
    private User usernameFollower;

    public FollowId() {
    }

    public FollowId(User usernameFollowed, User usernameFollower) {
        this.usernameFollower = usernameFollower;
        this.usernameFollowed = usernameFollowed;
    }

    public User getUsernameFollowed() {
        return usernameFollowed;
    }

    public void setUsernameFollowed(User usernameFollowed) {
        this.usernameFollowed = usernameFollowed;
    }

    public User getUsernameFollower() {
        return usernameFollower;
    }

    public void setUsernameFollower(User usernameFollower) {
        this.usernameFollower = usernameFollower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowId followId = (FollowId) o;
        return Objects.equals(usernameFollower, followId.usernameFollower) && Objects.equals(usernameFollowed, followId.usernameFollowed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameFollower, usernameFollowed);
    }

    
    @Override
    public FollowId clone(){
        FollowId aux;

        try{
            aux = (FollowId) super.clone();
        }catch (CloneNotSupportedException e) {
            aux = new FollowId();

            aux.setUsernameFollowed(this.getUsernameFollowed());
            aux.setUsernameFollower(this.getUsernameFollower());
        }

        return aux;
    }
}
