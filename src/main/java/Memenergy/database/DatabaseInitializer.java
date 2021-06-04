package Memenergy.database;

import Memenergy.data.Comment;
import Memenergy.data.Post;
import Memenergy.data.User;
import Memenergy.data.relations.CommentReport;
import Memenergy.data.relations.Follow;
import Memenergy.data.relations.Likes;
import Memenergy.data.relations.PostReport;
import Memenergy.database.services.*;
import Memenergy.database.services.relations.CommentReportService;
import Memenergy.database.services.relations.FollowService;
import Memenergy.database.services.relations.LikesService;
import Memenergy.database.services.relations.PostReportService;
import Memenergy.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Component
public class DatabaseInitializer {
    private static boolean initFinalized = true;

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    ImageService imageService;

    @Autowired
    PostReportService postReportService;
    @Autowired
    CommentReportService commentReportService;
    @Autowired
    FollowService followService;
    @Autowired
    LikesService likesService;

    @Autowired
    ExceptionEntityService exceptionEntityService;

    @Value(value = "${spring.jpa.hibernate.ddl-auto:}")
    private String initValue;
    @Value(value = "${database.exceptionEntity.clean}")
    private boolean deleteException;

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    private HashMap<Long,String> postExtension = new HashMap<>();
    private HashMap<String,String> userExtension = new HashMap<>();

    @PostConstruct
    public void postConstruct() throws IOException {
        if(deleteException){
            this.exceptionEntityService.deleteAll();
        }
        DatabaseInitializer.setInitFinalized(false);
        if(initValue.contains("create")){
            this.init();
        }
        DatabaseInitializer.setInitFinalized(true);
    }


    private void init() throws IOException {

        this.initMap();

        //Users
        User hellboy = this.userService.create(new User("Hellboy","email@Hellboy","1234",3)); //admin
        User jorgeGuillermo = this.userService.create(new User("JorgeGuillermo","email@JorgeGuillermo","1234"));
        User paco = this.userService.create(new User("Paco","email@Paco","1234"));
        User isidoro = this.userService.create(new User("Isidoro","email@Isidoro","1234"));
        User alfonso = this.userService.create(new User("Alfonso","email@Alfonso","1234",1));
        User luciana = this.userService.create(new User("Luciana","email@Luciana","1234"));
        User emilio = this.userService.create(new User("Emilio","email@Emilio","1234"));
        User nico = this.userService.create(new User("Nico","email@Nico","1234"));
        User miki = this.userService.create(new User("Miki","email@Miki","1234"));
        User misco = this.userService.create(new User("Misco","email@Misco","1234"));
        User bambu = this.userService.create(new User("Bambu","email@Bambu","1234"));
        User karen = this.userService.create(new User("Karen","email@Karen","SoOffended"));
        User cindyNero = this.userService.create(new User("CindyNero","email@CindyNero","0000"));

        //userImages
        this.saveImage("Hellboy");
        this.saveImage("CindyNero");
        this.saveImage("Bambu");
        this.saveImage("Isidoro");
        this.saveImage("JorgeGuillermo");
        this.saveImage("Karen");
        this.saveImage("Misco");
        this.saveImage("Paco");

        //posts
        Post p1 = this.postService.create(new Post("Cuando tu profesor dice que tiene la base de datos muy grande",":)",hellboy));
        Post p2 = this.postService.create(new Post("Sisoy",karen));
        Post p3 = this.postService.create(new Post("American police be like",hellboy));
        Post p4 = this.postService.create(new Post("Gato mantequilla","Gato mantequilla",hellboy));
        Post p5 = this.postService.create(new Post("Gato triste","Sisoy",hellboy));
        Post p6 = this.postService.create(new Post("Polisia","Así son :(",hellboy));
        Post p7 = this.postService.create(new Post("Admin triste","Admin triste hoy no hay meme",hellboy));
        Post p8 = this.postService.create(new Post("Kachau",hellboy));
        Post p9 = this.postService.create(new Post("Trolley problem","Yo lo haría",hellboy));
        Post p10 = this.postService.create(new Post("Messirve",hellboy));
        Post p11 = this.postService.create(new Post("Razones para vivir","Estas son mis razones para vivir",hellboy));
        Post p12 = this.postService.create(new Post("What if...",hellboy));
        Post p13 = this.postService.create(new Post("UwU",hellboy));
        Post p14 = this.postService.create(new Post("Gato enfadado","Gato enfadado",hellboy));
        Post p15 = this.postService.create(new Post("Gato chillido","Gato chillido",hellboy));
        Post p16 = this.postService.create(new Post("Bro","Bro",hellboy));
        Post p17 = this.postService.create(new Post("Rana","Una rana que bosteza xD",hellboy));
        Post p18 = this.postService.create(new Post("Domingo de resurreccion",hellboy));
        Post p19 = this.postService.create(new Post("Pepa pig xD",luciana));
        Post p20 = this.postService.create(new Post("Gato Cumple","Es su cumple, digan felicidades.",alfonso));
        Post p21 = this.postService.create(new Post("Gato duerme","Es un gato que duerme",emilio));
        Post p22 = this.postService.create(new Post("¿Me amarias aun sabiendo que soy una bolsa de cemento?",nico));
        Post p23 = this.postService.create(new Post("Sonic :(",isidoro));
        Post p24 = this.postService.create(new Post("Did you know...",isidoro));
        Post p25 = this.postService.create(new Post("Paco y yo",jorgeGuillermo));
        Post p26 = this.postService.create(new Post("Mi proximo truco",hellboy));

        //post images
        for (long i = 1; i<27; ++i){
            this.saveImage(i);
        }

        //comment
            //post 1
        Comment c1 = this.commentService.create(new Comment("No tenía nada que escuchar",hellboy,p1));
        Comment c2 = this.commentService.create(new Comment("At this time, Ánanda, I reside in the fullness of emptiness.",bambu,p1));
        Comment c3 = this.commentService.create(new Comment("this is, somehow, offensive",karen,p1));
        Comment c4 = this.commentService.create(new Comment("MISCOwater: Partners for Clean Water.",miki,p1));
        this.commentService.create(new Comment("Van a acabar a ostias",alfonso,p1));
        this.commentService.create(new Comment("@Bambu What is the plural of bambu? Bambi?",luciana,p1));
        this.commentService.create(new Comment("Misco was an IT brand whose ownership was split between Hilco Capital Limited and Systemax",misco,p1));
        this.commentService.create(new Comment("Sorry, I ain't got no money, I'm not trying to be funny, But I left it all at home today",cindyNero,p1));
        this.commentService.create(new Comment("Ostia tio, que no lo he enchufado",paco,p1));
        this.commentService.create(new Comment("Como se hacia para exportar este archivo?",luciana,p1));
        this.commentService.create(new Comment("Vale, olvidadlo, ya me acuerdo",luciana,p1));
        this.commentService.create(new Comment("Vale, no, se me ha vuelto a olvidar",luciana,p1));
            //post 2
        this.commentService.create(new Comment("Custom Audio Solutions. Stock Audio Solutions. Only MISCO offers USA and off-shore speaker driver and amplifier manufacturing combined with full turnkey service",misco,p2));
        this.commentService.create(new Comment("The precise working out of the results of kamma...",bambu,p2));
        this.commentService.create(new Comment("At Misco Home and Garden, we have an extensive variety of planters and boxes.",misco,p2));
        this.commentService.create(new Comment("Como se hace un laik en este fesbok? En mis tiempos esto no pasava",emilio,p2));
        this.commentService.create(new Comment("@Misco I want to speak with your manager",karen,p2));
        this.commentService.create(new Comment("@Bambu copy that",nico,p2));
        this.commentService.create(new Comment("Processing...",jorgeGuillermo,p2));
        this.commentService.create(new Comment("Sticker de gato mal",hellboy,p2));
        this.commentService.create(new Comment("@Misco I need a dollar, dollar, a dollar is what I need",cindyNero,p2));
            //post 3
        this.commentService.create(new Comment("Still offensive",karen,p3));
        this.commentService.create(new Comment("Que buen dato, pero nadie te ha preguntado",paco,p3));
        this.commentService.create(new Comment("In that case, Master Gotama, he does not reappear.",bambu,p3));
        this.commentService.create(new Comment("Ante la duda...",miki,p3));
        this.commentService.create(new Comment("...Doesn’t apply.",bambu,p3));
        this.commentService.create(new Comment("No digais eso que se me antoja",hellboy,p3));
        this.commentService.create(new Comment("Any feeling... Any perception... Any mental fabrication...",bambu,p3));
            //post 4
        this.commentService.create(new Comment("Then is there no self?",bambu,p4));
        this.commentService.create(new Comment("Persona vibes. Darkness, endless, despair, fear no more",luciana,p4));
        this.commentService.create(new Comment("if there is a self -- were to answer that there is a self, would that be in keeping with the arising of knowledge that all phenomena are not-self?",bambu,p4));
        this.commentService.create(new Comment("Coldness, blackened, no sound, feel no pain",luciana,p4));
        this.commentService.create(new Comment("No, lord.",bambu,p4));
        this.commentService.create(new Comment("Captured, helpless-- Ok vale :(",luciana,p4));
            //post 5
        this.commentService.create(new Comment("Si el problema es muy complejo, hay veces en que hay que invertarselo.",paco,p5));
            //post 6
        this.commentService.create(new Comment("I report slowly, slowly, slowly getting faster, Once I've started reporting it's really hard to stop, Faster, faster. It is so exciting!, I could report forever, report until I drop",karen,p6));
        this.commentService.create(new Comment("Este mensaje no constituye una amenaza",alfonso,p6));
        this.commentService.create(new Comment("El tribunal acepta los argumentos de la defensa",luciana,p6));
        this.commentService.create(new Comment("I'm not one of those who can easily hide, Don't have much money but boy if I did, I'd buy a big house where we both could live",cindyNero,p6));
        this.commentService.create(new Comment("Pues vaya patata",paco,p6));

        //commentReport
        this.commentReportService.create(new CommentReport(jorgeGuillermo,c1,"Too short"));
        this.commentReportService.create(new CommentReport(jorgeGuillermo,c2,"not relevant"));
        this.commentReportService.create(new CommentReport(jorgeGuillermo,c3,"Yes"));
        this.commentReportService.create(new CommentReport(karen,c3,"Offensive"));
        this.commentReportService.create(new CommentReport(hellboy,c3,"Karen"));
        this.commentReportService.create(new CommentReport(hellboy,c4,"Spam"));
        this.commentReportService.create(new CommentReport(jorgeGuillermo,c4,"Spam"));
        this.commentReportService.create(new CommentReport(paco,c4,"Spam"));
        this.commentReportService.create(new CommentReport(isidoro,c4,"Spam"));
        this.commentReportService.create(new CommentReport(alfonso,c4,"Spam"));
        this.commentReportService.create(new CommentReport(luciana,c4,"Spam"));
        this.commentReportService.create(new CommentReport(emilio,c4,"Spam"));
        this.commentReportService.create(new CommentReport(nico,c4,"Spam"));
        this.commentReportService.create(new CommentReport(miki,c4,"Spam"));
        this.commentReportService.create(new CommentReport(bambu,c4,"Thinking, 'He is going to aid people who are not dear or pleasing to me. But what should I expect?' one subdues hatred."));
        this.commentReportService.create(new CommentReport(karen,c4,"Spam"));
        this.commentReportService.create(new CommentReport(cindyNero,c4,"Spam"));

        //postReport
        this.postReportService.create(new PostReport(karen,p1,"So offended"));
        this.postReportService.create(new PostReport(karen,p2,"..."));
        this.postReportService.create(new PostReport(karen,p3,"Somehow offensive"));
        this.postReportService.create(new PostReport(karen,p4,"Somehow defensive"));
        this.postReportService.create(new PostReport(karen,p5,"Somehow"));
        this.postReportService.create(new PostReport(jorgeGuillermo,p7,"No"));
        this.postReportService.create(new PostReport(misco,p7,"Spam"));
        this.postReportService.create(new PostReport(bambu,p7,"In one of wrong mindfulness, wrong concentration arises."));
        this.postReportService.create(new PostReport(paco,p7,"Yo tiendo a los errores"));
        this.postReportService.create(new PostReport(emilio,p7,"Asi se le daba a like, ¿verdad?"));
        this.postReportService.create(new PostReport(karen,p7,"Karen"));
        this.postReportService.create(new PostReport(nico,p7,"Si"));
        this.postReportService.create(new PostReport(alfonso,p7,"Testing this"));
        this.postReportService.create(new PostReport(luciana,p7,"Testing this too"));
        this.postReportService.create(new PostReport(miki,p7,"Not Funny"));
        this.postReportService.create(new PostReport(cindyNero,p7,"Does not offer money"));
        this.postReportService.create(new PostReport(isidoro,p7,"Maybe"));

        //likes
        this.likesService.create(new Likes(alfonso,p1));
        this.likesService.create(new Likes(paco,p1));
        this.likesService.create(new Likes(hellboy,p1));
        this.likesService.create(new Likes(jorgeGuillermo,p1));
        this.likesService.create(new Likes(isidoro,p1));
        this.likesService.create(new Likes(luciana,p1));
        this.likesService.create(new Likes(emilio,p1));
        this.likesService.create(new Likes(nico,p1));
        this.likesService.create(new Likes(miki,p1));
        this.likesService.create(new Likes(misco,p1));
        this.likesService.create(new Likes(bambu,p1));
        this.likesService.create(new Likes(karen,p1));
        this.likesService.create(new Likes(cindyNero,p1));
        this.likesService.create(new Likes(hellboy,p2));
        this.likesService.create(new Likes(hellboy,p3));
        this.likesService.create(new Likes(hellboy,p4));
        this.likesService.create(new Likes(hellboy,p5));
        this.likesService.create(new Likes(hellboy,p6));
        this.likesService.create(new Likes(hellboy,p7));
        this.likesService.create(new Likes(hellboy,p8));
        this.likesService.create(new Likes(hellboy,p9));
        this.likesService.create(new Likes(hellboy,p10));
        this.likesService.create(new Likes(hellboy,p11));
        this.likesService.create(new Likes(isidoro,p11));
        this.likesService.create(new Likes(alfonso,p8));
        this.likesService.create(new Likes(alfonso,p7));
        this.likesService.create(new Likes(karen,p9));
        this.likesService.create(new Likes(karen,p6));
        this.likesService.create(new Likes(karen,p3));
        this.likesService.create(new Likes(nico,p2));
        this.likesService.create(new Likes(nico,p4));
        this.likesService.create(new Likes(nico,p6));
        this.likesService.create(new Likes(nico,p8));
        this.likesService.create(new Likes(nico,p10));
        this.likesService.create(new Likes(nico,p7));
        this.likesService.create(new Likes(luciana,p5));
        this.likesService.create(new Likes(bambu,p5));
        this.likesService.create(new Likes(bambu,p8));
        this.likesService.create(new Likes(bambu,p11));
        this.likesService.create(new Likes(isidoro,p7));

        //follow
        this.followService.create(new Follow(alfonso,hellboy));
        this.followService.create(new Follow(paco,hellboy));
        this.followService.create(new Follow(emilio,hellboy));
        this.followService.create(new Follow(jorgeGuillermo,hellboy));
        this.followService.create(new Follow(isidoro,hellboy));
        this.followService.create(new Follow(luciana,hellboy));
        this.followService.create(new Follow(nico,hellboy));
        this.followService.create(new Follow(miki,hellboy));
        this.followService.create(new Follow(misco,hellboy));
        this.followService.create(new Follow(bambu,hellboy));
        this.followService.create(new Follow(karen,hellboy));
        this.followService.create(new Follow(cindyNero,hellboy));
        this.followService.create(new Follow(karen,karen));
        this.followService.create(new Follow(hellboy,alfonso));
        this.followService.create(new Follow(nico,nico));
        this.followService.create(new Follow(luciana,alfonso));
        this.followService.create(new Follow(alfonso,luciana));
        this.followService.create(new Follow(hellboy,hellboy));
        this.followService.create(new Follow(luciana,miki));
        this.followService.create(new Follow(miki,luciana));
        this.followService.create(new Follow(bambu,alfonso));
        this.followService.create(new Follow(alfonso,bambu));
        this.followService.create(new Follow(karen,nico));
        this.followService.create(new Follow(nico,luciana));
        this.followService.create(new Follow(nico,isidoro));
    }

    private void saveImage(String username) throws IOException {
        this.imageService.saveImage(username,getImage("users",username),this.userExtension.get(username));
    }

    private void saveImage(long id) throws IOException {
        this.imageService.saveImage(id,getImage("posts",id),this.postExtension.get(id));
    }

    private void initMap(){

        this.userExtension.put("Hellboy","image/png");
        this.userExtension.put("CindyNero","image/png");
        this.userExtension.put("Bambu","image/jpeg");
        this.userExtension.put("Isidoro","image/jpeg");
        this.userExtension.put("JorgeGuillermo","image/jpeg");
        this.userExtension.put("Karen","image/jpeg");
        this.userExtension.put("Misco","image/jpeg");
        this.userExtension.put("Paco","image/jpeg");

        for (long i= 1 ; i<27;++i){
            this.postExtension.put(i,"image/jpeg");
        }
        this.postExtension.put((long)9,"image/png");

    }

    private Path createFilePath(long id, Path folder, String extension){
        return folder.resolve(id+"."+extension);
    }

    private Path createFilePath(String username, Path folder, String extension){
        return folder.resolve(username+"."+extension);
    }

    public byte[] getImage(String folderName, long id) throws IOException {
        UrlResource file;
        Path folder = FILES_FOLDER.resolve(folderName);
        file = new UrlResource(createFilePath(id, folder, this.postExtension.get(id).split("/")[1].equals("svg+xml")?this.postExtension.get(id).split("/")[1].split("\\+")[0]:this.postExtension.get(id).split("/")[1]).toUri());
        return file.getInputStream().readAllBytes();
    }

    public byte[] getImage(String folderName, String username) throws IOException {
        UrlResource file;
        Path folder = FILES_FOLDER.resolve(folderName);
        file = new UrlResource(createFilePath(username, folder, this.userExtension.get(username).split("/")[1].equals("svg+xml")?this.userExtension.get(username).split("/")[1].split("\\+")[0]:this.userExtension.get(username).split("/")[1]).toUri());
        return file.getInputStream().readAllBytes();
    }

    public static boolean isInitFinalized() {
        return initFinalized;
    }

    public static void setInitFinalized(boolean initFinalized) {
        DatabaseInitializer.initFinalized = initFinalized;
    }
}
