package jp.newbees.mastersip.model;

/**
 * Created by thangit14 on 2/14/17.
 */
public class EmailBackupItem {
    private String email;
    private String pass;
    private String extension;
    private String oldEmail;
    private String oldPass;

    public EmailBackupItem(String email, String pass, String extension) {
        this.email = email;
        this.pass = pass;
        this.extension = extension;
    }

    public EmailBackupItem() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }
}
