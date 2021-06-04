package Memenergy.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;


@Entity
public class User implements Comparable<User>,Cloneable {
    @Id
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean profileImage;
    @Column(length = 1024)
    private String description;
    @Column(length = 2700)
    private Calendario birthDate;
    private String country;
    @Column(nullable = false)
    private int privileges;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = false;
        this.description = null;
        this.birthDate = null;
        this.country = null;
        this.privileges = 0;
    }

    public User(String username, String email, String password, int privileges) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = false;
        this.description = null;
        this.birthDate = null;
        this.country = null;
        this.privileges = privileges;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasProfileImage() {
        return profileImage;
    }

    public void setProfileImage(boolean profileImage) {
        this.profileImage = profileImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendario getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Calendario birthDate) {
        this.birthDate = birthDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonIgnore
    public int getPrivileges() {
        return privileges;
    }

    @JsonProperty
    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }

    @JsonIgnore
    public List<String> getRoles() {
        List<String> roles = new LinkedList<>();

        if((privileges&1)==1){ //admin principal
            roles.add("ROLE_admin");
        }
        if((privileges&2)==2){ //exception handler
            roles.add("ROLE_exceptionHandler");
        }

        return roles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (email.equals(other.email))
            return true;
        if (username == null) {
            return other.username == null;
        } else return username.equalsIgnoreCase(other.username);
    }


    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }

    
    @Override
    public User clone(){
        User aux;

        try{
            aux = (User) super.clone();
        } catch (CloneNotSupportedException e){
            aux = new User();

            aux.setUsername(this.getUsername());
            aux.setEmail(this.getEmail());
            aux.setPassword(this.getPassword());
            aux.setProfileImage(this.hasProfileImage());
            aux.setDescription(this.getDescription());
            aux.setBirthDate(this.getBirthDate());
            aux.setCountry(this.getCountry());
            aux.setPrivileges(this.getPrivileges());
        }

        return aux;
    }
}
