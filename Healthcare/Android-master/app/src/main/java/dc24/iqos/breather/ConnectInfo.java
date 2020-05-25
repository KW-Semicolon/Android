package dc24.iqos.breather;

import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectInfo  {

    File file  = new File("/file.txt");
    FileWriter fw = null;
    String text = "This is TEST String";
    public static int count = 0;
    public static int all_time=0;
    public long Start = 0;
    public long End = 0;
    public Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("    MM월                 dd일                 hh시 mm분");

    public ConnectInfo()
    {
        count ++;
    }
    public void SaveLog(TextView textView2)
    {
        try{
            fw = new FileWriter(file);
            fw.write(getStartTime());
            fw.close();
            textView2.setText("io success");
        }
        catch (Exception e)
        {
            textView2.setText("io fail");
            e.printStackTrace();
        }
    }

    public String getStartTime()
    {
        Start = System.currentTimeMillis();
        mDate = new Date(Start);
        return mFormat.format(mDate);
    }

    public String getEndTime()
    {
        End = System.currentTimeMillis();
        mDate = new Date(End);
        return mFormat.format(mDate);
    }

    public long getSmokeTime()
    {
        return ((End - Start)/1000);
    }

    public long getAlltime(long time)
    {
        this.all_time += time;
        return all_time;
    }

}
