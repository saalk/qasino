``` mermaid
sequenceDiagram 
	participant AP as Application
	participant BE as ApplyCardService
	participant LB as ListBalances
	participant QC as QualificationChecks
	participant PA as ProductAgreement
	participant IP as InvolvedParty
	participant LA as ListAccount
	participant IT as IncomeTransaction
	participant BKR as BKR
	participant SC as ScoreCheck
	participant AWA as WorkflowApprovalAPI
	participant CC as CreateCard
	participant CDOT as ConnectDOT

	Note over AP,BE: BasePath = /consumer-credit-cards/requests
 	
 	activate AP
	AP->>+BE: POST: /apply-card/direct-apply
	BE-->>-AP: requestId, idempotencyKey
	
	AP->>+BE: POST: /apply-card/authorize
	
	BE->>+QC: POST: /api/qualification-checks/parties
	QC-->>-BE: OK / NOK + list of findings
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
    
    BE->>+CDOT: POST: /message/send/with-response
    CDOT-->>-BE: OK / NOK
	
    deactivate BE
```

