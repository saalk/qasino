package applyextra.commons.model.financialdata.creditlimit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import applyextra.commons.model.SourceOfIncome;

import java.io.IOException;

/**
 * Jackson takes the integer value passed and calculates the value assuming it to be ordinal this deserializer uses
 * value and code and get name of enum member
 * 
 * @author KQ62IO 10 apr. 2017
 */
public class SourceofIncomeEnumDeserializer extends JsonDeserializer {

    @Override
    public SourceOfIncome deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {

        SourceOfIncome type = SourceOfIncome.fromCode(jp.getValueAsString());
        if (type != null) {
            return type;
        }
        throw new com.fasterxml.jackson.core.JsonParseException(jp,
                "failed to parse SourceOfIncome enum : " + jp.getValueAsString());
    }

}
