/// score : [{"scoreId":12345,"quizId":123,"created":"2019-02-18T03:22:56.637Z","updated":"2020-02-18T03:22:56.637Z","hintsTaken":1,"computedAnswer":{"answeredCount":1,"correctCount":1,"currentQuestion":2,"currentIndex":1,"currentPercentToPass":10,"passed":false},"answers":[{"questionId":111,"answer":"t","secondsToAnswer":10}],"user":{"username":"tester"}}]

class Score {
  List<Score> _score;

  List<Score> get score => _score;

  Score({
      List<Score> score}){
    _score = score;
}

  Score.fromJson(dynamic json) {
    if (json['score'] != null) {
      _score = [];
      json['score'].forEach((v) {
        _score.add(Score.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (_score != null) {
      map['score'] = _score.map((v) => v.toJson()).toList();
    }
    return map;
  }

}

/// scoreId : 12345
/// quizId : 123
/// created : "2019-02-18T03:22:56.637Z"
/// updated : "2020-02-18T03:22:56.637Z"
/// hintsTaken : 1
/// computedAnswer : {"answeredCount":1,"correctCount":1,"currentQuestion":2,"currentIndex":1,"currentPercentToPass":10,"passed":false}
/// answers : [{"questionId":111,"answer":"t","secondsToAnswer":10}]
/// user : {"username":"tester"}

class Score {
  int _scoreId;
  int _quizId;
  String _created;
  String _updated;
  int _hintsTaken;
  Computed _computed;
  List<Answers> _answers;
  User _user;

  int get scoreId => _scoreId;
  int get quizId => _quizId;
  String get created => _created;
  String get updated => _updated;
  int get hintsTaken => _hintsTaken;
  Computed get computedAnswer => _computed;
  List<Answers> get answers => _answers;
  User get user => _user;

  Score({
      int scoreId, 
      int quizId, 
      String created, 
      String updated, 
      int hintsTaken, 
      Computed computedAnswer,
      List<Answers> answers, 
      User user}){
    _scoreId = scoreId;
    _quizId = quizId;
    _created = created;
    _updated = updated;
    _hintsTaken = hintsTaken;
    _computed = computedAnswer;
    _answers = answers;
    _user = user;
}

  Score.fromJson(dynamic json) {
    _scoreId = json['scoreId'];
    _quizId = json['quizId'];
    _created = json['created'];
    _updated = json['updated'];
    _hintsTaken = json['hintsTaken'];
    _computed = json['computedAnswer'] != null ? Computed.fromJson(json['computedAnswer']) : null;
    if (json['answers'] != null) {
      _answers = [];
      json['answers'].forEach((v) {
        _answers.add(Answers.fromJson(v));
      });
    }
    _user = json['user'] != null ? User.fromJson(json['user']) : null;
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['scoreId'] = _scoreId;
    map['quizId'] = _quizId;
    map['created'] = _created;
    map['updated'] = _updated;
    map['hintsTaken'] = _hintsTaken;
    if (_computed != null) {
      map['computedAnswer'] = _computed.toJson();
    }
    if (_answers != null) {
      map['answers'] = _answers.map((v) => v.toJson()).toList();
    }
    if (_user != null) {
      map['user'] = _user.toJson();
    }
    return map;
  }

}

/// username : "tester"

class User {
  String _username;

  String get username => _username;

  User({
      String username}){
    _username = username;
}

  User.fromJson(dynamic json) {
    _username = json['username'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['username'] = _username;
    return map;
  }

}

/// questionId : 111
/// answer : "t"
/// secondsToAnswer : 10

class Answers {
  int _questionId;
  String _answer;
  int _secondsToAnswer;

  int get questionId => _questionId;
  String get answer => _answer;
  int get secondsToAnswer => _secondsToAnswer;

  Answers({
      int questionId, 
      String answer, 
      int secondsToAnswer}){
    _questionId = questionId;
    _answer = answer;
    _secondsToAnswer = secondsToAnswer;
}

  Answers.fromJson(dynamic json) {
    _questionId = json['questionId'];
    _answer = json['answer'];
    _secondsToAnswer = json['secondsToAnswer'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['questionId'] = _questionId;
    map['answer'] = _answer;
    map['secondsToAnswer'] = _secondsToAnswer;
    return map;
  }

}

/// answeredCount : 1
/// correctCount : 1
/// currentQuestion : 2
/// currentIndex : 1
/// currentPercentToPass : 10
/// passed : false

class Computed {
  int _answeredCount;
  int _correctCount;
  int _currentQuestion;
  int _currentIndex;
  int _currentPercentToPass;
  bool _passed;

  int get answeredCount => _answeredCount;
  int get correctCount => _correctCount;
  int get currentQuestion => _currentQuestion;
  int get currentIndex => _currentIndex;
  int get currentPercentToPass => _currentPercentToPass;
  bool get passed => _passed;

  Computed({
      int answeredCount, 
      int correctCount, 
      int currentQuestion, 
      int currentIndex, 
      int currentPercentToPass, 
      bool passed}){
    _answeredCount = answeredCount;
    _correctCount = correctCount;
    _currentQuestion = currentQuestion;
    _currentIndex = currentIndex;
    _currentPercentToPass = currentPercentToPass;
    _passed = passed;
}

  Computed.fromJson(dynamic json) {
    _answeredCount = json['answeredCount'];
    _correctCount = json['correctCount'];
    _currentQuestion = json['currentQuestion'];
    _currentIndex = json['currentIndex'];
    _currentPercentToPass = json['currentPercentToPass'];
    _passed = json['passed'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['answeredCount'] = _answeredCount;
    map['correctCount'] = _correctCount;
    map['currentQuestion'] = _currentQuestion;
    map['currentIndex'] = _currentIndex;
    map['currentPercentToPass'] = _currentPercentToPass;
    map['passed'] = _passed;
    return map;
  }

}