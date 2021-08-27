/*
 * Swagger Quizzes
 *
 * This is a sample quizzes server.
 */

const klass = require('./routes/klass.js');

/* Route definition styles:
 *
 *	define(path, method, function)
 *	soap(path, soapAction, function)
 *
 */
Sandbox.define('/klass/quizzes/1.0.0/quizzes', 'POST', klass.postQuiz);
Sandbox.define('/klass/quizzes/1.0.0/quizzes', 'GET', klass.getQuizFindbyfilter);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/favorite', 'GET', klass.getQuizFindbyfavorite);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/followed', 'GET', klass.getQuizFindbyfollowed);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}', 'GET', klass.getQuiz);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}/favorite', 'PUT', klass.putQuizFavorite);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}', 'PUT', klass.putQuiz);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}', 'PUT', klass.putQuiz);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}', 'DELETE', klass.deleteQuiz);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}/favorite', 'DELETE', klass.deleteQuizFavorite);
Sandbox.define('/klass/quizzes/1.0.0/quizzes/{quizId}/questions', 'POST', klass.postQuizQuestions);
Sandbox.define('/klass/quizzes/1.0.0/tags', 'GET', klass.getTags);
Sandbox.define('/klass/quizzes/1.0.0/scores', 'POST', klass.postScore);
Sandbox.define('/klass/quizzes/1.0.0/scores/{scoreId}', 'GET', klass.getScore);
Sandbox.define('/klass/quizzes/1.0.0/scores/{scoreId}', 'PUT', klass.putScore);
Sandbox.define('/klass/quizzes/1.0.0/scores/{scoreId}', 'DELETE', klass.deleteScore);
Sandbox.define('/klass/quizzes/1.0.0/scores/{scoreId}/answers', 'POST', klass.postScoreAnswers);
Sandbox.define('/klass/quizzes/1.0.0/user', 'POST', klass.postUser);
Sandbox.define('/klass/quizzes/1.0.0/profile', 'GET', klass.getProfile);
Sandbox.define('/klass/quizzes/1.0.0/user/login', 'POST', klass.getUserLogin);
Sandbox.define('/klass/quizzes/1.0.0/user/logout', 'GET', klass.getUserLogout);
Sandbox.define('/klass/quizzes/1.0.0/user', 'GET', klass.getUser);
Sandbox.define('/klass/quizzes/1.0.0/user', 'PUT', klass.putUser);
Sandbox.define('/klass/quizzes/1.0.0/user', 'DELETE', klass.deleteUser);
