package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.database.converter.LocalDateAtributeConverter;
import nl.ing.sc.creditcardmanagement.commonobjects.HousingCostsType;
import nl.ing.sc.creditcardmanagement.commonobjects.MaritalStatus;
import nl.ing.sc.creditcardmanagement.commonobjects.SourceOfIncome;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name =  "CC_FINANCIAL_DATA")
public class FinancialDataEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "INCOME_SOURCE")
    @Enumerated(EnumType.STRING)
    private SourceOfIncome incomeSource;

    @Column(name = "MONTHLY_NET_INCOME")
    private BigDecimal monthlyNetIncome;

    @Column(name = "MONTHLY_ALIMONY")
    private BigDecimal monthlyAlimony;

    @Column(name = "HAS_CHILDREN")
    private Boolean childrenPresent;

    @Column(name = "YEAR_FULL_INCOME")
    private Boolean incomeFullLastYear;

    @Column(name = "HOUSING_COSTS_TYPE")
    @Enumerated(EnumType.STRING)
    private HousingCostsType housingCostsType;

    @Column(name = "MONTHLY_HOUSING_COSTS")
    private BigDecimal monthlyHousingCosts;

    @Column(name = "MARITAL_STATUS")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "START_CONTRACT_DATE")
    private LocalDate startContractDate;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "END_CONTRACT_DATE")
    private LocalDate endContractDate;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "TIME_SINCE_FREELANCER")
    private LocalDate timeSinceFreelancer;

    @Column(name = "COST_PAID_WITH_ING")
    private Boolean costPaidWithING;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_FINANCIAL_DATA")
    private List<ExtraLoanEntity> extraLoanEntities = new ArrayList<>();
}
