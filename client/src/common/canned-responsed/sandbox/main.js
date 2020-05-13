/*
 * POST /klass/quizzes/1.0.0/quiz
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 */
exports.postQuiz = function(req, res){
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
};

/*
 * GET /klass/quizzes/1.0.0/quiz
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * author(type: string) - query parameter - Author values that need to be considered for filter
 */
exports.getQuizFindbyfilter = function(req, res){
    // 	req.checkQuery('author', 'Invalid query parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    
    // set response body and send
    // res.json({});
    res.render('quizzes');
};

/*
 * GET /klass/quizzes/1.0.0/quiz/findByFavorite
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * user(type: string) - query parameter - User values that need to be considered for filter
 */
exports.getQuizFindbyfavorite = function(req, res){
    req.checkQuery('user', 'Invalid query parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('quiz456');
};

/*
 * GET /klass/quizzes/1.0.0/quiz/findByFollowed
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * user(type: string) - query parameter - User values that need to be considered for filter
 */
exports.getQuizFindbyfollowed = function(req, res){
    req.checkQuery('user', 'Invalid query parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('quiz123');
};

/*
 * GET /klass/quizzes/1.0.0/quiz/{quizId}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * quizId(type: string) - path parameter - ID of quiz to return
 */
exports.getQuiz = function(req, res){
    req.check('quizId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('quiz123');
};

/*
 * PUT /klass/quizzes/1.0.0/quiz/{quizId}
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 * quizId(type: string) - path parameter - ID of quiz to update
 */
exports.putQuiz = function(req, res){
    req.check('quizId', 'Invalid parameter').notEmpty();
    req.check('quizId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
};

/*
 * DELETE /klass/quizzes/1.0.0/quiz/{quizId}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * api_key(type: string) - header parameter
 * quizId(type: string) - path parameter - quiz id to delete
 */
exports.deleteQuiz = function(req, res){
    req.check('quizId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({
        message: 'al fine'
    });
};

/*
 * POST /klass/quizzes/1.0.0/quiz/{quizId}/questions
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 * quizId(type: string) - path parameter - ID of quiz to update
 */
exports.postQuizQuestions = function(req, res){
    req.check('quizId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
    
    
};

/*
 * GET /klass/quizzes/1.0.0/tags
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 */
exports.getTags = function(req, res){
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('tags');
};

/*
 * POST /klass/quizzes/1.0.0/score
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 */
exports.postScore = function(req, res){
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
};

/*
 * GET /klass/quizzes/1.0.0/score/{scoreId}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * scoreId(type: string) - path parameter - ID of score to return
 */
exports.getScore = function(req, res){
    req.check('scoreId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.render('score');
    
};

/*
 * PUT /klass/quizzes/1.0.0/score/{scoreId}
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 * scoreId(type: string) - path parameter - ID of score to update
 */
exports.putScore = function(req, res){
    req.check('scoreId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({
        message: 'al fine'
    });
};

/*
 * DELETE /klass/quizzes/1.0.0/score/{scoreId}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * api_key(type: string) - header parameter
 * scoreId(type: string) - path parameter - score id to delete
 */
exports.deleteScore = function(req, res){
    req.check('scoreId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');

    // set response body and send
    res.json({
        message: 'al fine'
    });
};

/*
 * POST /klass/quizzes/1.0.0/score/{scoreId}/answers
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 * scoreId(type: string) - path parameter - ID of score to update
 */
exports.postScoreAnswers = function(req, res){
    req.check('scoreId', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json')
    
    // set response body and send
    res.json({
        message: 'al fine'
    });
};

/*
 * POST /klass/quizzes/1.0.0/user
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 */
exports.postUser = function(req, res){
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
};

/*
 * GET /klass/quizzes/1.0.0/profile/{username}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * username(type: string) - path parameter - The name that needs to be fetched. Use user1 for testing.
 */
exports.getProfile = function(req, res){
    req.check('username', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('profile');
};

/*
 * GET /klass/quizzes/1.0.0/user/login
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * username(type: string) - query parameter - The user name for login
 * password(type: string) - query parameter - The password for login in clear text
 */
exports.getUserLogin = function(req, res){
    // req.checkQuery('username', 'Invalid query parameter').notEmpty();
    // req.checkQuery('password', 'Invalid query parameter').notEmpty();
    var user = req.body.user;
    
    // if (user.email !== undefined) {
    //     return returnError(res, 400, "No Email");
    // }
    
    // res.set('X-Rate-Limit', 'some value');
    // res.set('X-Expires-After', 'some value');
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('user');
};

/*
 * GET /klass/quizzes/1.0.0/user/logout
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 */
exports.getUserLogout = function(req, res) {
	res.status(200);

	// set response body and send
	res.json({});
};

/*
 * GET /klass/quizzes/1.0.0/user/{username}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * username(type: string) - path parameter - The name that needs to be fetched. Use user1 for testing.
 */
exports.getUser = function(req, res){
    // req.check('username', 'Invalid parameter').notEmpty();
    // if (req.validationErrors()) {
    //     return res.json(400, req.validationErrorsJson());
    // }
    res.status(200);
    
    // set response body and send
    res.type('json');
    res.render('user');
};

/*
 * PUT /klass/quizzes/1.0.0/user/{username}
 *
 * Parameters (body params accessible on req.body for JSON, req.xmlDoc for XML):
 *
 * username(type: string) - path parameter - name that need to be updated
 */
exports.putUser = function(req, res){
    req.check('username', 'Invalid parameter').notEmpty();
    if (req.validationErrors()) {
        return res.json(400, req.validationErrorsJson());
    }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({ message: 'al fine' });
};

/*
 * DELETE /klass/quizzes/1.0.0/user/{username}
 *
 * Parameters (named path params accessible on req.params and query params on req.query):
 *
 * username(type: string) - path parameter - The name that needs to be deleted
 */
exports.deleteUser = function(req, res){
    // req.check('username', 'Invalid parameter').notEmpty();
    // if (req.validationErrors()) {
    //     return res.json(400, req.validationErrorsJson());
    // }
    res.status(200);
    res.set('Content-Type', 'application/json');
    res.type('application/json');
    
    // set response body and send
    res.json({
        message: 'al fine'
    });
};