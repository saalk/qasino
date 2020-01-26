package applyextra.commons.model.database.converter;

import applyextra.operations.model.PackageType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PackageTypeConverter implements AttributeConverter<PackageType, String> {

    @Override
    public String convertToDatabaseColumn(final PackageType packageType) {
        if (packageType != null) {
            return packageType.toString();
        }
        return null;
    }

    @Override
    public PackageType convertToEntityAttribute(final String packageName) {
        return PackageType.byString(packageName);
    }

}
