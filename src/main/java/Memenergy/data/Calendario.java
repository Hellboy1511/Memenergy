package Memenergy.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings("serial")

//just to have a specific toString (without overriding)
@JsonDeserialize(using = CalendarioDeserializer.class)
public class Calendario extends GregorianCalendar {
    public Calendario() {
        super();
    }

    public static Calendario fromString(String timestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date d = sdf.parse(timestamp.replaceAll("\\+0([0-9]){1}\\:00", "+0$100"));
        Calendario calendar = new Calendario();
        calendar.setTime(d);
        return calendar;
    }

    public static Calendario fromHtlmDate(Date timestamp){
        if(timestamp == null) return null;
        else {
            Calendario calendario = new Calendario();
            calendario.setTime(timestamp);
            return calendario;
        }
    }

    public String dateToString() {
        int mes=this.get(GregorianCalendar.MONTH)+1;
        return this.get(GregorianCalendar.YEAR) + "/" + mes + "/" +this.get(GregorianCalendar.DAY_OF_MONTH)+ " " + this.get(GregorianCalendar.HOUR_OF_DAY) + ":" + ((this.get(GregorianCalendar.MINUTE)<10)?("0"+this.get(GregorianCalendar.MINUTE)):this.get(GregorianCalendar.MINUTE));
    }

    public String dateToStringNoHour(){
        int mes=this.get(GregorianCalendar.MONTH)+1;
        return this.get(GregorianCalendar.YEAR) + "/" + mes + "/" +this.get(GregorianCalendar.DAY_OF_MONTH);
    }
}
