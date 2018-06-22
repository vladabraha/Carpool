package cz.uhk.fim.brahavl1.carpoolv4.Model;

public class UidEmail {

    private String email;
    private String uid;

    public UidEmail(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public UidEmail() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
