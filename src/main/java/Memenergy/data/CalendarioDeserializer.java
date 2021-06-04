package Memenergy.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.util.GregorianCalendar;

public class CalendarioDeserializer extends StdDeserializer<GregorianCalendar> {
    public CalendarioDeserializer() {
        this(null);
    }

    protected CalendarioDeserializer(Class<GregorianCalendar> t) {
        super(t);
    }

    @Override
    public GregorianCalendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        try{
            return Calendario.fromString(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
