package applyextra.commons.model.database.entity;

import lombok.Getter;
import applyextra.commons.model.ProductType;
import applyextra.commons.model.database.converter.PackageTypeConverter;
import applyextra.commons.model.database.converter.ProductTypeConverter;
import applyextra.operations.model.PackageType;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "CC_ANNUAL_PERCENTAGE_COST", uniqueConstraints = {@UniqueConstraint(columnNames = {"PACKAGE_TYPE", "PRODUCT_TYPE", "CREDIT_AMOUNT"})})
public class CreditCardAnnualPercentageCost {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;
    @Column(name = "PACKAGE_TYPE", nullable = false)
    @Convert(converter = PackageTypeConverter.class)
    private PackageType packageType;
    @Column(name = "PRODUCT_TYPE", nullable = false)
    @Convert(converter = ProductTypeConverter.class)
    private ProductType productType;
    @Column(name = "CREDIT_AMOUNT", nullable = false)
    private Integer creditAmount;
    @Column(name = "JKP", nullable = false)
    private BigDecimal jkp;

}
