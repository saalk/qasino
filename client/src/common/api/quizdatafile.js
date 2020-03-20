/* eslint-disable no-tabs */
/**
 * Mocking client-server processing
 */
const quizDataFile = `
{
	"quiz": {
		"metadata": {
      "id": 123,
			"title": "x quiz",
			"description": "Test your basic product x knowledge",
      "subject": "Product x",
      "audiance": "engineers",
			"level": "basic knowledge"
		},
		"settings": {
      "final": "t",
			"maxSecondsPerQuestion": "5",
			"numberOfHints": "2",
			"allowExit": "f",
			"allowGoBack": "f",
			"randomizeQuestions": "t",
      "randomizeAnswers": "t",
      "minimumToPass": "60"
		},
		"questions": [{
				"text": "Is product x hosted by a third party outside ING?",
				"category": "trueFalse",
				"answer": "t",
				"explanation": "Product x is being administrated at a third party located in y"
			},
			{
				"text": "Which picture of available options for product x has the beste features?",
				"category": "imagesChoice",
				"images": [{
						"src": "https://a.png",
						"alt": "description of a when not loaded",
						"value": "a"
					},
					{
						"src": "https://a.png",
						"alt": "description of b when not loaded",
						"value": "b"
					},
					{
						"src": "https://a.png",
						"alt": "description of c when not loaded",
						"value": "c"
					}
				],
				"answer": "c",
				"explanation": "Variant c has in total z features, more then any of the other variants"
			},
			{
				"text": "Which product variant is shown in this picture?",
				"category": "image",
				"image": {
					"src": "https://a.png",
					"alt": "description of a when not loaded",
					"value": "a"
				},
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
				"answer": "a",
				"explanation": "Variant a matches the image best"
			},
			{
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
				"text": "Order the product variant from most to least features",
				"category": "putInCorrectOrder",
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
				"answer": "cab",
				"explanation": "Variant a has in total z features more then b which in turn has more features than c"
			},
			{
				"text": "Which product has which feature?",
				"category": "matchThePairs",
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
				"explanation": "Variant c has in total z features more then the other variants"
			},
			{
				"text": "Decribe the slogan to attract customers for product x, you need to mention at least 2 key elements",
				"category": "textEssay",
				"answers": ["easy", "easily", "cheap", "low priced", "robust"],
				"atLeastNeeded": "2",
				"explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
			},
			{
				"text": "Product x is ___ and ___ when used and amongst the cheapest on the market",
				"category": "fillTheBlanks",
				"answersOrdered": ["easy", "robust"],
				"explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
			},
			{
				"text": "What is the minimum age to apply for porduct x?",
				"category": "numerical",
				"answer": "18",
				"explanation": "You should be at least 18 to apply but for a partner 16 is the minimum"
			},
			{
				"text": "What are the key elements of porduct x?",
				"category": "multiAnswer",
				"choices": [{
						"label": "Easy to use",
						"value": "easy"
					},
					{
						"label": "Expensive when compared",
						"value": "expensive"
					},
					{
						"label": "5 star ratings",
						"value": "robust"
					}
				],
				"answers": ["easy", "robust"],
				"explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
			}
		]
	}
}
`;

let quizMetaData = {
  id: 123,
  title: 'x quiz',
  description: 'Test your basic product x knowledge',
  subject: 'product x',
  audiance: 'engineers',
  level: 'basic knowledge',
};
let quizSettings = {
  fianl: 't',
  maxSecondsPerQuestion: '5',
  numberOfHints: '2',
  allowExit: 'f',
  allowGoBack: 'f',
  randomizeQuestions: 't',
  randomizeAnswers: 't',
  minimumToPass: '60',
};
// trueFalse
// let trueFalseQuestion = {
//   text: 'Is product x hosted by a third party outside ING?',
//   category: 'trueFalse',
//   answer: 't',
//   explanation: 'Product x is being administrated at a third party located in y',
// };
// image
// let imagechoiceQuestion = {
//   text: 'Which product variant is shown in this picture?',
//   category: 'image',
//   image: {
//     src: 'https://a.png',
//     alt: 'description of a when not loaded',
//     value: 'a',
//   },
//   choices: [
//     {
//       label: 'Variant a',
//       value: 'a',
//     },
//     {
//       label: 'Variant b',
//       value: 'b',
//     },
//     {
//       label: 'Variant c',
//       value: 'c',
//     },
//   ],
//   answer: 'a',
//   explanation: 'Variant a matches the image best',
// };
// multiChoice
// let mulitChoiceQuestion = {
//   text: 'Which product variant of product x has the beste features?',
//   category: 'multiChoice',
//   choices: [
//     {
//       label: 'Variant a',
//       value: 'a',
//     },
//     {
//       label: 'Variant b',
//       value: 'b',
//     },
//     {
//       label: 'Variant c',
//       value: 'c',
//     },
//   ],
//   answer: 'c',
//   explanation: 'Variant c has in total z features more then the other variants',
// };
// imagesChoice
// putInCorrectOrder
// matchThePairs
// textEssay
// fillTheBlanks
// numerical
// multiAnswer

// let quizResult = {
//   quizProgress: 'intro',
//   currentQuestion: '0',
//   startDateTime: '',
//   endDateTime: '',
//   totalPauzeTime: '',
//   answers: [],
//   correct: '0',
//   hintsTaken: '0',
//   percentageCorrect: '61',
// };

let quizQuestions = {};
let quiz = {};

const QuizDataFile = {
  mounted() {
    this.loadData('123');
  },
  methods: {
    loadData(inputId) {
      const id = inputId;
      // let quiz = {};
      if (id !== '') {
        quiz = JSON.parse(quizDataFile);
      } else {
        // error invalid
      }
      quizMetaData = quiz.quizMetaData;
      quizSettings = quiz.quizSettings;
      quizQuestions = quiz.questions;
    },
    // getters
    getQuiz() {
      return quiz;
    },
    getQuizMetaData() {
      return quizMetaData;
    },
    getQuizSettings() {
      return quizSettings;
    },
    getQuizQuestions() {
      return quizQuestions;
    },
  },
};

export default QuizDataFile;
