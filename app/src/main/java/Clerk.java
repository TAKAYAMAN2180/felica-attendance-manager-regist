import java.util.Date;

public class Clerk {
    private String name;
    private String idm;
    private Date time;
    private boolean isEntry;

    Clerk(String idm, String name, boolean isEntry) {
        this.time = new Date();
        this.idm = idm;
        this.name = name;
        this.isEntry = isEntry;
    }

    Clerk(String idm, boolean isEntry) {
        this(idm, idm, isEntry);
    }

    public String getIdm() {
        return idm;
    }

    public boolean getIsEntry() {
        return isEntry;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return this.time;
    }

    public boolean getEntry() {
        return this.isEntry;
    }
}
