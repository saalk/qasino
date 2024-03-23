# Business rules 

* student card rules
  * balance on selected current account needs to be positive
  * selected current account is `Studentenrekening`
  * max limit of 1000
* non-student card rules
  * requested limit is not higher than allowed limit (*)
    * correct down to the highest allowed limit (regular flow only)
  * selected current account is `Betaalrekening` or `En/of betaalrekening`
  * scoring check is not RED for platinum card (*)
  * max limit of 50000
* existing card rules
  * the total amount of main credit cards for the customer is less than 3 (*)
  * the selected current account has no credit card linked to it (*)
* package rules
  * selected current account with RoyaalPakket is only allowed to have platinum cards (*)
  * selected current account with BetaalPakket is not allowed to have platinum cards (*)
  * selected current account needs to be linked to a valid package (*)
* product limit
  * credit card max limit of 5000 (*)
  * platinum card max limit of 20000 (*)
  * in regular flow the selected limit wil be corrected down if it exceeds the max limit
* allowed limit
  * for credit card and platinum card the allowed limit is calculated using the limit table
  * for student card the limit will be 1000 in case:
    * with overdraft arrangements: the detected income is at least 500 euros 
    * without overdraft arrangements: the detected income is at least 250 euros
  * in case the income is not enough to get an allowed limit of 1000, the allowed limit will become 50 euros (low limit card)
  * in case the scoring check is RED for credit card or student card, the allowed limit will become 50 euros (low limit card)
* personal details rules
  * residential address is in NL (*)
  * customer at least 18 years old (*) (covered in EAU4)
* beneficiary rules
  * only exception process is allowed to apply for family
  * beneficiary at least 16 years old
  * requester and beneficiary can't be the same customer
  * student card is not allowed to have extra cards
* direct apply flow
  * allowed limit is 1000 or higher: limit of 1000 euros
  * allowed limit is lower as 1000: limit of 50 euros (low limit card)
* other rules
  * no apply card request for selected current account was authorised in the last 5 days (*)
    * no request in pending state
    * no fulfilled in the last 5 days
  * customer has no in bank account in transfer arrangements (*)
  * BKR check is green (*)

(*) Can be overruled in exception process