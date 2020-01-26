package applyextra.commons.model.database.converter;

import applyextra.commons.model.ProductType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

    @Override
    public String convertToDatabaseColumn(final ProductType productType) {
        if (productType != null) {
            return productType.getProductType();
        }
        return null;
    }

    @Override
    public ProductType convertToEntityAttribute(final String productType) {
        return ProductType.string2Code(productType);
    }

}
