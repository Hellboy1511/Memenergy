package Memenergy.data.images;

import Memenergy.data.Post;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity

public class PostImage implements Serializable {

    @Id
    private long id;
    @OneToOne(optional = false) @MapsId
    @JoinColumn(nullable = false ,foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (post_id) references post(id) on delete cascade on update cascade"))
    private Post post;

    @Column(nullable = false, length = 60000000)//~50MB
    private byte[] image;
    @Column(nullable = false)
    private String extension;

    public PostImage() {
    }

    public PostImage(Post post, byte[] image, String extension) {
        this.id = post.getId();
        this.post = post;
        this.image = image;
        this.extension = extension;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
        PostImage postImage = (PostImage) o;
        return id == postImage.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
