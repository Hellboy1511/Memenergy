package Memenergy.database.services;

import Memenergy.data.ExceptionEntity;
import Memenergy.database.repositories.ExceptionEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ExceptionEntityService {

    @Autowired
    ExceptionEntityRepository exceptionEntityRepository;
    @Autowired
    EntityManager entityManager;

    public ExceptionEntity get(long id){
        try {
            return this.entityManager.createQuery("select e from ExceptionEntity e where e.id=:id",ExceptionEntity.class).setParameter("id",id).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public ExceptionEntity create(ExceptionEntity exceptionEntity){
        return this.exceptionEntityRepository.saveAndFlush(exceptionEntity);
    }

    public Collection<ExceptionEntity> getAll() {
        return this.entityManager.createQuery("select e from ExceptionEntity e",ExceptionEntity.class).getResultList().parallelStream().sorted().collect(Collectors.toList());
    }

    @Transactional
    public ExceptionEntity delete(long id) {
        ExceptionEntity e = this.get(id);
        if(e!=null){
            if (this.entityManager.createQuery("delete from ExceptionEntity e where e.id=:id").setParameter("id",id).executeUpdate()>0){
                return e;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    @Transactional
    public int deleteAll() {
        return this.entityManager.createQuery("delete from ExceptionEntity e").executeUpdate();
    }
}
