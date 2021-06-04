package Memenergy.api;

import Memenergy.data.ExceptionEntity;
import Memenergy.database.services.ExceptionEntityService;
import Memenergy.exceptions.exceptions.api.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class ExceptionEntityController {
    @Autowired
    ExceptionEntityService exceptionEntityService;

    @GetMapping("/api/error/{id}")
    public ResponseEntity<ExceptionEntity> get(@PathVariable long id){
        ExceptionEntity exceptionEntity = this.exceptionEntityService.get(id);
        if(exceptionEntity == null) throw new NotFoundException();
        return new ResponseEntity<>(exceptionEntity, HttpStatus.OK);
    }

    @GetMapping("/api/errors")
    public ResponseEntity<Collection<ExceptionEntity>> get(){
        return new ResponseEntity<>(this.exceptionEntityService.getAll(), HttpStatus.OK);
    }

    @DeleteMapping("/api/error/{id}")
    public ResponseEntity<ExceptionEntity> delete(@PathVariable long id){
        ExceptionEntity exceptionEntity = this.exceptionEntityService.delete(id);
        if(exceptionEntity == null) throw new NotFoundException();
        return new ResponseEntity<>(exceptionEntity, HttpStatus.OK);
    }
}
