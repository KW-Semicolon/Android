package dc24.iqos.breather;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String DATE = "DATE";
        public static final String TIME = "TIME";
        public static final String SBP = "SBP";
        public static final String DBP = "DBP";
        public static final String HEART = "HEART";
        public static final String RESP = "RESP";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +DATE+" text not null , "
                +TIME+" text not null , "
                +SBP+" integer not null , "
                +DBP+" integer not null , "
                +HEART+" integer not null , "
                +RESP+" integer not null );";
    }
}