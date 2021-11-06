import java.util.Date;

public class Clerk {
    private String studentNum;
    private String name;
    private Date time;
    private boolean hasEntrance = false;

    Clerk(String studentNum,String name, boolean hasEntrance) {
        this.studentNum = studentNum;
        this.time = new Date();
        this.name = name;
        this.hasEntrance = hasEntrance;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public String getName() {
        return this.name;
    }

    public Date getTime() {
        return this.time;
    }

    public boolean getHasEntrance() {
        return this.hasEntrance;
    }
}
