package dc24.iqos.breather;

public class TempData {
    private String date;
    private String time;
    private long sbp;
    private long dbp;
    private long heart;
    private long resp;

    TempData(){}

    TempData(String date, String time, long sbp, long dbp, long heart, long resp){
        this.date=date;
        this.time=time;
        this.sbp=sbp;
        this.dbp=dbp;
        this.heart=heart;
        this.resp=resp;
    }

    String getDate(){
        return this.date;
    }
    String getTime(){
        return this.time;
    }
    Long getSbp(){
        return this.sbp;
    }
    Long getDbp(){
        return this.dbp;
    }
    Long getHeart(){
        return this.heart;
    }
    Long getResp(){
        return this.resp;
    }

    void setDate(String newdate){
        this.date = newdate;
    }

    void setTime(String newtime){
        this.time = newtime;
    }
}
