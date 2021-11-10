package Models;

import java.util.List;

public class User {

    private String email;
    private String firstName;
    private String lastName;
    private String uid;
    private List<Object> listOfCapturedImages;

    public User(String f, String l, String e,String u ){
        this.firstName = f;
        this.lastName = l;
        this.email = e;
        this.uid = u;
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getUid(){
        return uid;
    }

}
