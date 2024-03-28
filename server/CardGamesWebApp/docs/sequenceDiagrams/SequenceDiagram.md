``` mermaid
sequenceDiagram 
	participant USER as User
	participant BE as ApplyCardService
	participant CAI as CurrentAccountInquire
	participant QC as QualificationChecks
	participant PA as ProductAgreement
	participant IP as InvolvedParty
	participant LA as ListAccount
	participant IT as IncomeTransaction
	participant BKR as BKR
	participant SC as ScoreCheck
	participant AWA as WorkflowApprovalAPI
	participant LB as ListBalances
	participant CC as CreateCard
	participant CDOT as ConnectDOT

	Note over USER,BE: BasePath = /consumer-credit-cards/requests
 	
 	activate USER
	USER->>+BE: POST: /apply-card/initiate
	BE->>+CAI: GET: /deposit-accounts/current-accounts/me
	CAI-->>-BE: List of CurrentAccountInquire details
	BE->>+QC: POST: /api/qualification-checks/parties
	QC-->>-BE: OK / NOK + list of findings
	BE-->>-USER: requestId
	
	USER->>+BE: POST: /apply-card/allowed
	alt beneficiary present
        BE->>+QC: POST: POST /api/qualification-checks/parties
        QC-->>-BE: OK / NOK + list of findings
    end   
    BE->>+CAI: GET: /deposit-accounts/current-accounts/me
    CAI-->>-BE: List of CurrentAccountInquire details like balance
    BE->>+PA: GET: /v6/agreements/products
    PA-->>-BE: List of arrangements
    BE->>+PA: GET: /v4/agreements/products/{id}/agreementRelationships
    PA-->>-BE: List of agreementRelationships
    BE->>+IP: GET: /v6/involved-parties/{uuid}
    IP-->>-BE: List of personnal detail
    
    BE->>BE: Check Business rules
	BE-->>-USER: requestId
	
	USER->>+BE: POST: /apply-card/creditscore
	BE->>+BKR: POST: /credit-bureau/nl/solvency
	BKR-->>-BE: OK / NOK + list of findings
	BE->>+SC: POST: /credit-score/scores
	SC-->>-BE: OK / NOK + list of findings
	BE-->>-USER: requestId
	
	USER->>+BE: POST: /apply-card/creditlimit
	BE->>+LA: GET: /consumer-credit-cards/credit-cards/{personId}
    LA-->>-BE: List of Accounts
    BE->>+IT: POST:	/performance/income/v2/current-accounts
    IT-->>-BE: List of income transaction
    
    BE->>BE: Check Business rules
	BE-->>-USER: requestId
    
	USER->>+BE: POST: /apply-card/{id}/submit
	BE->>+AWA: POST:  /approval-requests
	AWA-->>-BE: referenceId
	BE-->>-USER: referenceId, requestId

    USER->>+AWA: Approves
	AWA-->>USER: OK
 	deactivate USER
	AWA->>+BE: POST: /apply-card/authorize
	deactivate AWA
	
	BE->>+QC: POST: /api/qualification-checks/parties
	QC-->>-BE: OK / NOK + list of findings
	alt beneficiary present
        BE->>+QC: POST: POST /api/qualification-checks/parties
        QC-->>-BE: OK / NOK + list of findings
    end   
    BE->>+PA: GET: /v6/agreements/products
    PA-->>-BE: List of arrangements
    BE->>+LB: POST: /current-accounts/balances/actual
    LB-->>-BE: Balance of current account
    BE->>+PA: GET: /v4/agreements/products/{id}/agreementRelationships
    PA-->>-BE: List of agreementRelationships
    BE->>+IP: GET: /v6/involved-parties/{uuid}
    IP-->>-BE: List of personnal detail
    BE->>BE: Check Business rules
    BE->>+BKR: POST: /credit-bureau/nl/solvency
	BKR-->>-BE: OK / NOK + list of findings
	BE->>+SC: POST: /credit-score/scores
	SC-->>-BE: OK / NOK + list of findings
	BE->>+LA: GET: /consumer-credit-cards/credit-cards/{personId}
    LA-->>-BE: List of Accounts
    BE->>+IT: POST:	/performance/income/v2/current-accounts
    IT-->>-BE: List of income transaction
    
    BE->>BE: Check Business rules
	
    BE->>+CC: POST: /consumer-credit-cards/requests/create-card
    CC-->>-BE: SIA Response
	
	alt (it is exception flow & customer message not disabled) or (it is not exception flow)
        BE->>+CDOT: POST: /message/send/with-response
        CDOT-->>-BE: OK / NOK
    end
    deactivate BE
```

