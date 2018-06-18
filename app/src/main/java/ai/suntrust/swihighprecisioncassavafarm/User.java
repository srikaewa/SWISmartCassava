package ai.suntrust.swihighprecisioncassavafarm;

public class User {
    private String uid;
    private String displayName;
    private String email;
    private String photoURL;

    User()
    {

    }

    void   setEmail(String email){ this.email = email;}
    String getEmail(){ return email;}
    void   setUID(String uid){ this.uid = uid;}
    String getUID(){ return uid;}
    void   setDisplayName(String displayName){ this.displayName = displayName;}
    String getDisplayName(){ return displayName;}
    void   setPhotoURL(String photoURL){ this.photoURL = photoURL;}
    String getPhotoURL(){ return photoURL;}
}
