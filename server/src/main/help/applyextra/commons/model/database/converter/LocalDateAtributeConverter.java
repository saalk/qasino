package applyextra.commons.model.database.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;

@Converter
public class LocalDateAtributeConverter implements AttributeConverter<LocalDate, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(final LocalDate attribute) {
        return attribute != null ? Timestamp.valueOf(attribute.atStartOfDay()) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(final Timestamp dbData) {
        return dbData != null ? dbData.toLocalDateTime().toLocalDate() : null;
    }

}
