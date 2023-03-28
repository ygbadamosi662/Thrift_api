package Handler;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public class DateHandler extends StdDeserializer<Date>
{
    public DateHandler()
    {
        this(null);
    }

    public DateHandler(Class<?> clazz)
    {
        super(clazz);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException
    {
        String date = jsonParser.getText();
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(date);
        }catch(Exception e)
        {
            return null;
        }

    }
}
