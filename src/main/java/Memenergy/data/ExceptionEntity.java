package Memenergy.data;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class ExceptionEntity implements Comparable<ExceptionEntity>,Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    private List<String> name;
    @ElementCollection
    private List<String> message;
    @ElementCollection
    private List<String> stackTrace;
    @ManyToOne
    private User userLogged;
    private int statusCode;
    private String path;
    private String error;

    @Column(length = 2700,nullable = false)
    private Calendario dateTime;

    public ExceptionEntity() {
        this.dateTime  = new Calendario();
        this.message = new LinkedList<>();
        this.stackTrace = new LinkedList<>();
        this.name = new LinkedList<>();
    }

    public ExceptionEntity(Throwable e,User userLogged,int statusCode, String error, String path) {
        this.dateTime=new Calendario();
        this.message = new LinkedList<>();
        this.stackTrace = new LinkedList<>();
        this.name = new LinkedList<>();


        this.userLogged = userLogged;
        this.statusCode = statusCode;
        this.error = error;
        this.path = path;

        while(e!=null){
            this.name.add(e.getClass().toString());
            this.message.add(e.getLocalizedMessage()==null?" ":e.getLocalizedMessage());

            for (StackTraceElement st :
                    e.getStackTrace()) {
                this.stackTrace.add(st.toString()+"\n");
            }
            this.stackTrace.add("\n\n\n");
            e = e.getCause();

        }

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Calendario getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendario dateTime) {
        this.dateTime = dateTime;
    }

    public User getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(User userLogged) {
        this.userLogged = userLogged;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public int compareTo(ExceptionEntity o) {
        return -this.dateTime.compareTo(o.dateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExceptionEntity exceptionEntity = (ExceptionEntity) o;
        return id == exceptionEntity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ExceptionEntity aux;
        try {
            aux = (ExceptionEntity) super.clone();
        }catch (CloneNotSupportedException e){
            aux = new ExceptionEntity();
            aux.setDateTime(this.getDateTime());
            aux.setId(this.getId());
            aux.setMessage(this.getMessage());
            aux.setName(this.getName());
            aux.setStackTrace(this.getStackTrace());
        }
        return aux;
    }
}
