package Memenergy.data.images;

import Memenergy.data.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class UserImage implements Serializable {

    @Id
    private String username;

    @OneToOne(optional = false) @MapsId
    @JoinColumn(foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (user_username) references user(username) on delete cascade on update cascade"))
    private User user;

    @Column(nullable = false, length = 60000000)//~50MB
    private byte[] image;
    @Column(nullable = false)
    private String extension;

    public UserImage() {
    }

    public UserImage(User user, byte[] image, String extension) {
        this.username=user.getUsername();
        this.user = user;
        this.image = image;
        this.extension = extension;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImage userImage = (UserImage) o;
        return Objects.equals(username, userImage.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
