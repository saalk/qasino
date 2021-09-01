# Quiz Model

## quiz 
```json
{
	"quiz": {
		"quizId": 123,
		"meta": {
			"title": "x quiz",
			"description": "Test your basic product x knowledge",
			"subject": "Product x",
			"audiance": "engineers",
			"created": "2016-02-18T03:22:56.637Z",
			"updated": "2016-02-18T03:48:35.824Z",
			"tagList": ["basic", "x"]
		},
		"computed": {
			"favorited": true,
			"favoritesCount": 123
		},
		"author": {
			"username": "tester",
			"bio": "Test user api",
			"image": "https://api.hello-avatar.com/adorables/avatars/280",
			"computed": {
				"following": false,
				"followingCount": 285
			}
		},
		"settings": {
			"final": "t",
			"maxSecondsPerQuestion": 5,
			"numberOfHints": 2,
			"allowExit": "f",
			"allowGoBack": "f",
			"randomizeQuestions": "t",
			"randomizeAnswers": "t",
			"minimumPercentToPass": 60
		},
		"questions": [{
				"questionId": 111,
				"text": "Is product x hosted by a third party outside ING?",
				"category": "trueFalse",
				"choices": null,
				"answer": "t",
				"explanation": "Product x is being administrated at a third party located in y"
			},
			{
				"questionId": 222,
				"text": "Which product variant of product x has the beste features?",
				"category": "multiChoice",
				"choices": [{
						"label": "Variant a",
						"value": "a"
					},
					{
						"label": "Variant b",
						"value": "b"
					},
					{
						"label": "Variant c",
						"value": "c"
					}
				],
				"answer": "c",
				"explanation": "Variant c has in total z features more then the other variants"
			},
			{
				"questionId": 333,
				"text": "What is the minimum age to apply for porduct x?",
				"category": "numerical",
				"choices": null,
				"answer": "18",
				"explanation": "You should be at least 18 to apply but for a partner 16 is the minimum"
			}
		]
	}
}
```

## score 
```json
{
	"score": [{
		"scoreId": 12345,
		"quizId": 123,
		"created": "2019-02-18T03:22:56.637Z",
		"updated": "2020-02-18T03:22:56.637Z",
		"hintsTaken": 1,
		"computed": {
			"answeredCount": 1,
			"correctCount": 1,
			"currentQuestion": 2,
			"currentIndex": 1,
			"currentPercentToPass": 10,
			"passed": false
		},
		"answers": [{
			"questionId": 111,
			"answer": "t",
			"secondsToAnswer": 10
		}],
		"user": {
			"username": "tester"
		}
	}]
}
```

## profile 
```json
{
	"profile": {
		"userId": 123,
		"email": "test@test.com",
		"token": "jwt.token.here",
		"username": "visitor",
		"userNameSequence": 1,
		"created": "2016-02-18T03:22:56.637Z",
		"day": 2,
		"week": "5",
		"bio": "Visitor profiles api",
		"image": "https://api.hello-avatar.com/adorables/avatars/288",
		"securedLoan": 100,
		"computed": {
			"following": true,
			"followingCount": 285
		}
	}
}
```

## tags 
```json
{
	"tags": ["basic", "x", "y"]
}
```

## user 
```json
{
	"user": {
		"userId": 123,
		"email": "test@test.com",
		"token": "jwt.token.here",
		"username": "tester",
		"userNameSequence": 1,
		"created": "2016-02-18T03:22:56.637Z",
		"day": 2,
		"week": "5",
		"bio": "Test login api",
		"image": "https://api.hello-avatar.com/adorables/avatars/285",
		"securedLoan": 100
	}
}
```