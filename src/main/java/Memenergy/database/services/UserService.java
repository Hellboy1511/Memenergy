package Memenergy.database.services;

import Memenergy.data.User;
import Memenergy.database.DatabaseInitializer;
import Memenergy.database.repositories.UserRepository;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    LoggedUser loggedUser;
    @Autowired
    UserRepository repository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    //get a user given its username
    public User get(String username) {
        try {
        return this.entityManager.createQuery("select u from User u where u.username=:username", User.class).setParameter("username",username).getSingleResult().clone();
        } catch (NoResultException e){
            return null;
        }
    }

    //create a new user
    public User create(User user) {
        if (this.get(user.getUsername())!=null) {
            return null;
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return repository.saveAndFlush(user);
        }
    }

    //update a user given its username
    @Transactional
    public User update(String username, User updatedUser) {
        User actualUser = this.get(username);
        if(actualUser != null){
            boolean change = false;
            boolean emailChanged = false;
            boolean passwordChanged = false;
            boolean privilegesChanged = false;
            boolean countryChanged = false;
            boolean descriptionChanged = false;
            boolean birthDateChanged = false;
            boolean profileImageChanged = false;

            StringBuilder sb = new StringBuilder();
            sb.append("update User u set ");
            if(!(updatedUser.getEmail()==null || Objects.equals(actualUser.getEmail(),updatedUser.getEmail()))){
                if(this.getByEmail(updatedUser.getEmail())!=null){
                    return null;
                }
                actualUser.setEmail(updatedUser.getEmail());
                sb.append("u.email=:email");
                emailChanged = true;
                change = true;
            }
            if(!(updatedUser.getPassword()==null || Objects.equals(actualUser.getPassword(),updatedUser.getPassword()))){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.password=:password");
                actualUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                passwordChanged = true;
            }
            if(!Objects.equals(actualUser.getPrivileges(),updatedUser.getPrivileges())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.privileges=:privileges");
                actualUser.setPrivileges(updatedUser.getPrivileges());
                privilegesChanged = true;
            }
            if(!Objects.equals( actualUser.getCountry(),updatedUser.getCountry())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.country=:country");
                actualUser.setCountry(updatedUser.getCountry());
                countryChanged = true;
            }
            if(!Objects.equals(actualUser.getDescription(),updatedUser.getDescription())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.description=:description");
                actualUser.setDescription(updatedUser.getDescription());
                descriptionChanged = true;
            }
            if(!Objects.equals(actualUser.getBirthDate(),updatedUser.getBirthDate())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.birthDate=:birthDate");
                actualUser.setBirthDate(updatedUser.getBirthDate());
                birthDateChanged = true;
            }
            if(!Objects.equals(actualUser.hasProfileImage(),updatedUser.hasProfileImage())){
                if(change){
                    sb.append(", ");
                }
                change = true;
                sb.append("u.profileImage=:profileImage");
                profileImageChanged = true;
                actualUser.setProfileImage(updatedUser.hasProfileImage());
            }


            if(change){
                sb.append(" where u.username=:username");
                Query query = this.entityManager.createQuery(sb.toString()).setParameter("username",username);
                if(emailChanged){
                    query.setParameter("email",updatedUser.getEmail());
                }
                if(passwordChanged){
                    query.setParameter("password",passwordEncoder.encode(updatedUser.getPassword()));
                }
                if(privilegesChanged){
                    query.setParameter("privileges",updatedUser.getPrivileges());
                }
                if(countryChanged){
                    query.setParameter("country",updatedUser.getCountry());
                }
                if(descriptionChanged){
                    query.setParameter("description", updatedUser.getDescription());
                }
                if(birthDateChanged){
                    query.setParameter("birthDate",updatedUser.getBirthDate());
                }
                if(profileImageChanged){
                    query.setParameter("profileImage",updatedUser.hasProfileImage());
                }
                if (query.executeUpdate() > 0) {
                    if (DatabaseInitializer.isInitFinalized()&&loggedUser.getLoggedUser().equals(actualUser)){
                        loggedUser.setLoggedUser(actualUser);
                    }
                    return actualUser;
                } else {
                    return null;
                }
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    //delete a user given its username
    @Transactional
    public User delete(String username) {
        User aux =this.get(username);
        if(aux!=null){
            List<Long> postsReport = this.entityManager.createQuery("select p.id from Post p join PostReport pr on p.id = pr.postReportId.id.id where pr.postReportId.username.username = :username", Long.class).setParameter("username",username).getResultList();
            List<Long> comments = this.entityManager.createQuery("select c.id from Comment c join CommentReport cr on c.id = cr.commentReportId.id.id where cr.commentReportId.username.username = :username", Long.class).setParameter("username",username).getResultList();
            List<Long> postsLike = this.entityManager.createQuery("select p.id from Post p join Likes l on p.id = l.likesId.id.id where l.likesId.username.username = :username", Long.class).setParameter("username",username).getResultList();

            if(this.entityManager.createQuery("delete from User u where u.username=:username").setParameter("username",username).executeUpdate()>0){
                Long reportCounter;
                Long likeCounter;
                for (Long l :
                        postsLike) {
                    likeCounter = this.entityManager.createQuery("select count(l) from Likes l where l.likesId.id.id = :id",Long.class).setParameter("id",l).getSingleResult();
                    this.entityManager.createQuery("update Post p set p.likeCounter = :likeCounter where p.id = :id").setParameter("id",l).setParameter("likeCounter",likeCounter).executeUpdate();
                }
                for (Long l :
                        postsReport) {
                    reportCounter = this.entityManager.createQuery("select count(pr) from PostReport pr where pr.postReportId.id.id = :id",Long.class).setParameter("id",l).getSingleResult();
                    this.entityManager.createQuery("update Post p set p.reportCounter = :reportCounter where p.id = :id").setParameter("id",l).setParameter("reportCounter",reportCounter).executeUpdate();
                }
                for (Long l :
                        comments) {
                    reportCounter = this.entityManager.createQuery("select count(cr) from CommentReport cr where cr.commentReportId.id.id = :id",Long.class).setParameter("id",l).getSingleResult();
                    this.entityManager.createQuery("update Comment c set  c.reportCounter = :reportCounter where c.id = :id").setParameter("id",l).setParameter("reportCounter",reportCounter).executeUpdate();
                }

                return aux;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //get 10 users given a page, may return null or less than 10 users
    public Collection<User> get(long page) {
        Stream<User> stream;
        List<User> aux = this.entityManager.createQuery("select u from User u", User.class).getResultList();
        List<User> listFinal = new LinkedList<>();
        page--;
        stream = aux.parallelStream().sorted().sequential();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = stream.skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }
    }

    //get a user given its email
    public User getByEmail(String email) {
        try {
            return this.entityManager.createQuery("select u from User u where u.email=:email", User.class).setParameter("email", email).getSingleResult().clone();
        } catch (NoResultException e){
            return null;
        }
    }

    public Collection<User> filtered(String username, long page) {
        List<User> aux;
        List<User> listFinal = new LinkedList<>();
        page--;
        aux = this.entityManager.createQuery("select u from User u where u.username like :username", User.class).setParameter("username","%"+username+"%").getResultList();
        long size = aux.parallelStream().count();
        if (page * 10 + 1 > size) {
            return null;
        } else {
            long n = 10;
            if (size - page * 10 < 10) {
                n = size - page * 10;
            }
            aux = aux.parallelStream().sorted().sequential().skip(page * 10).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                listFinal.add(aux.get(i));
            }
            return listFinal;
        }
    }
}
