package dc24.iqos.breather;

public class TempChartDate {
    private String Date ="";

    TempChartDate(){ }
    TempChartDate(String Date){
        this.Date=Date;
    }

    String getDate(){ return this.Date; }

    void setDate(String date){this.Date=date;}
}
