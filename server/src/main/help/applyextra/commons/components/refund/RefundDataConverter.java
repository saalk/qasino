package applyextra.commons.components.refund;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RefundDataConverter implements AttributeConverter<RefundData,String> {

    @Override
    public String convertToDatabaseColumn(RefundData attribute) {
        return attribute.getJson();
    }

    @Override
    public RefundData convertToEntityAttribute(String dbData) {
        return new RefundData().setJson(dbData);
    }

}
