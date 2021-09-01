/// quiz : {"quizId":123,"meta":{"title":"x quiz","description":"Test your basic product x knowledge","subject":"Product x","audiance":"engineers","created":"2016-02-18T03:22:56.637Z","updated":"2016-02-18T03:48:35.824Z","tagList":["basic","x"]},"computed":{"favorited":true,"favoritesCount":123},"author":{"username":"tester","bio":"Test user api","image":"https://api.hello-avatar.com/adorables/avatars/280","computed":{"following":false,"followingCount":285}},"settings":{"final":"t","maxSecondsPerQuestion":5,"numberOfHints":2,"allowExit":"f","allowGoBack":"f","randomizeQuestions":"t","randomizeAnswers":"t","minimumPercentToPass":60},"questions":[{"questionId":111,"text":"Is product x hosted by a third party outside ING?","category":"trueFalse","choices":null,"answer":"t","explanation":"Product x is being administrated at a third party located in y"},{"questionId":222,"text":"Which product variant of product x has the beste features?","category":"multiChoice","choices":[{"label":"Variant a","value":"a"},{"label":"Variant b","value":"b"},{"label":"Variant c","value":"c"}],"answer":"c","explanation":"Variant c has in total z features more then the other variants"},{"questionId":333,"text":"What is the minimum age to apply for porduct x?","category":"numerical","choices":null,"answer":"18","explanation":"You should be at least 18 to apply but for a partner 16 is the minimum"}]}

class Quiz {
  Quiz _quiz;

  Quiz get quiz => _quiz;

  Quiz({
      Quiz quiz}){
    _quiz = quiz;
}

  Quiz.fromJson(dynamic json) {
    _quiz = json['quiz'] != null ? Quiz.fromJson(json['quiz']) : null;
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (_quiz != null) {
      map['quiz'] = _quiz.toJson();
    }
    return map;
  }

}

/// quizId : 123
/// meta : {"title":"x quiz","description":"Test your basic product x knowledge","subject":"Product x","audiance":"engineers","created":"2016-02-18T03:22:56.637Z","updated":"2016-02-18T03:48:35.824Z","tagList":["basic","x"]}
/// computed : {"favorited":true,"favoritesCount":123}
/// author : {"username":"tester","bio":"Test user api","image":"https://api.hello-avatar.com/adorables/avatars/280","computed":{"following":false,"followingCount":285}}
/// settings : {"final":"t","maxSecondsPerQuestion":5,"numberOfHints":2,"allowExit":"f","allowGoBack":"f","randomizeQuestions":"t","randomizeAnswers":"t","minimumPercentToPass":60}
/// questions : [{"questionId":111,"text":"Is product x hosted by a third party outside ING?","category":"trueFalse","choices":null,"answer":"t","explanation":"Product x is being administrated at a third party located in y"},{"questionId":222,"text":"Which product variant of product x has the beste features?","category":"multiChoice","choices":[{"label":"Variant a","value":"a"},{"label":"Variant b","value":"b"},{"label":"Variant c","value":"c"}],"answer":"c","explanation":"Variant c has in total z features more then the other variants"},{"questionId":333,"text":"What is the minimum age to apply for porduct x?","category":"numerical","choices":null,"answer":"18","explanation":"You should be at least 18 to apply but for a partner 16 is the minimum"}]

class Quiz {
  int _quizId;
  Meta _meta;
  Computed _computed;
  Author _author;
  Settings _settings;
  List<Questions> _questions;

  int get quizId => _quizId;
  Meta get meta => _meta;
  Computed get computed => _computed;
  Author get author => _author;
  Settings get settings => _settings;
  List<Questions> get questions => _questions;

  Quiz({
      int quizId, 
      Meta meta, 
      Computed computed, 
      Author author, 
      Settings settings, 
      List<Questions> questions}){
    _quizId = quizId;
    _meta = meta;
    _computed = computed;
    _author = author;
    _settings = settings;
    _questions = questions;
}

  Quiz.fromJson(dynamic json) {
    _quizId = json['quizId'];
    _meta = json['meta'] != null ? Meta.fromJson(json['meta']) : null;
    _computed = json['computed'] != null ? Computed.fromJson(json['computed']) : null;
    _author = json['author'] != null ? Author.fromJson(json['author']) : null;
    _settings = json['settings'] != null ? Settings.fromJson(json['settings']) : null;
    if (json['questions'] != null) {
      _questions = [];
      json['questions'].forEach((v) {
        _questions.add(Questions.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['quizId'] = _quizId;
    if (_meta != null) {
      map['meta'] = _meta.toJson();
    }
    if (_computed != null) {
      map['computed'] = _computed.toJson();
    }
    if (_author != null) {
      map['author'] = _author.toJson();
    }
    if (_settings != null) {
      map['settings'] = _settings.toJson();
    }
    if (_questions != null) {
      map['questions'] = _questions.map((v) => v.toJson()).toList();
    }
    return map;
  }

}

/// questionId : 111
/// text : "Is product x hosted by a third party outside ING?"
/// category : "trueFalse"
/// choices : null
/// answer : "t"
/// explanation : "Product x is being administrated at a third party located in y"

class Questions {
  int _questionId;
  String _text;
  String _category;
  dynamic _choices;
  String _answer;
  String _explanation;

  int get questionId => _questionId;
  String get text => _text;
  String get category => _category;
  dynamic get choices => _choices;
  String get answer => _answer;
  String get explanation => _explanation;

  Questions({
      int questionId, 
      String text, 
      String category, 
      dynamic choices, 
      String answer, 
      String explanation}){
    _questionId = questionId;
    _text = text;
    _category = category;
    _choices = choices;
    _answer = answer;
    _explanation = explanation;
}

  Questions.fromJson(dynamic json) {
    _questionId = json['questionId'];
    _text = json['text'];
    _category = json['category'];
    _choices = json['choices'];
    _answer = json['answer'];
    _explanation = json['explanation'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['questionId'] = _questionId;
    map['text'] = _text;
    map['category'] = _category;
    map['choices'] = _choices;
    map['answer'] = _answer;
    map['explanation'] = _explanation;
    return map;
  }

}

/// final : "t"
/// maxSecondsPerQuestion : 5
/// numberOfHints : 2
/// allowExit : "f"
/// allowGoBack : "f"
/// randomizeQuestions : "t"
/// randomizeAnswers : "t"
/// minimumPercentToPass : 60

class Settings {
  String _final;
  int _maxSecondsPerQuestion;
  int _numberOfHints;
  String _allowExit;
  String _allowGoBack;
  String _randomizeQuestions;
  String _randomizeAnswers;
  int _minimumPercentToPass;

  String get final => _final;
  int get maxSecondsPerQuestion => _maxSecondsPerQuestion;
  int get numberOfHints => _numberOfHints;
  String get allowExit => _allowExit;
  String get allowGoBack => _allowGoBack;
  String get randomizeQuestions => _randomizeQuestions;
  String get randomizeAnswers => _randomizeAnswers;
  int get minimumPercentToPass => _minimumPercentToPass;

  Settings({
      String final, 
      int maxSecondsPerQuestion, 
      int numberOfHints, 
      String allowExit, 
      String allowGoBack, 
      String randomizeQuestions, 
      String randomizeAnswers, 
      int minimumPercentToPass}){
    _final = final;
    _maxSecondsPerQuestion = maxSecondsPerQuestion;
    _numberOfHints = numberOfHints;
    _allowExit = allowExit;
    _allowGoBack = allowGoBack;
    _randomizeQuestions = randomizeQuestions;
    _randomizeAnswers = randomizeAnswers;
    _minimumPercentToPass = minimumPercentToPass;
}

  Settings.fromJson(dynamic json) {
    _final = json['final'];
    _maxSecondsPerQuestion = json['maxSecondsPerQuestion'];
    _numberOfHints = json['numberOfHints'];
    _allowExit = json['allowExit'];
    _allowGoBack = json['allowGoBack'];
    _randomizeQuestions = json['randomizeQuestions'];
    _randomizeAnswers = json['randomizeAnswers'];
    _minimumPercentToPass = json['minimumPercentToPass'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['final'] = _final;
    map['maxSecondsPerQuestion'] = _maxSecondsPerQuestion;
    map['numberOfHints'] = _numberOfHints;
    map['allowExit'] = _allowExit;
    map['allowGoBack'] = _allowGoBack;
    map['randomizeQuestions'] = _randomizeQuestions;
    map['randomizeAnswers'] = _randomizeAnswers;
    map['minimumPercentToPass'] = _minimumPercentToPass;
    return map;
  }

}

/// username : "tester"
/// bio : "Test user api"
/// image : "https://api.hello-avatar.com/adorables/avatars/280"
/// computed : {"following":false,"followingCount":285}

class Author {
  String _username;
  String _bio;
  String _image;
  Computed _computed;

  String get username => _username;
  String get bio => _bio;
  String get image => _image;
  Computed get computed => _computed;

  Author({
      String username, 
      String bio, 
      String image, 
      Computed computed}){
    _username = username;
    _bio = bio;
    _image = image;
    _computed = computed;
}

  Author.fromJson(dynamic json) {
    _username = json['username'];
    _bio = json['bio'];
    _image = json['image'];
    _computed = json['computed'] != null ? Computed.fromJson(json['computed']) : null;
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['username'] = _username;
    map['bio'] = _bio;
    map['image'] = _image;
    if (_computed != null) {
      map['computed'] = _computed.toJson();
    }
    return map;
  }

}

/// following : false
/// followingCount : 285

class Computed {
  bool _following;
  int _followingCount;

  bool get following => _following;
  int get followingCount => _followingCount;

  Computed({
      bool following, 
      int followingCount}){
    _following = following;
    _followingCount = followingCount;
}

  Computed.fromJson(dynamic json) {
    _following = json['following'];
    _followingCount = json['followingCount'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['following'] = _following;
    map['followingCount'] = _followingCount;
    return map;
  }

}

/// favorited : true
/// favoritesCount : 123

class Computed {
  bool _favorited;
  int _favoritesCount;

  bool get favorited => _favorited;
  int get favoritesCount => _favoritesCount;

  Computed({
      bool favorited, 
      int favoritesCount}){
    _favorited = favorited;
    _favoritesCount = favoritesCount;
}

  Computed.fromJson(dynamic json) {
    _favorited = json['favorited'];
    _favoritesCount = json['favoritesCount'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['favorited'] = _favorited;
    map['favoritesCount'] = _favoritesCount;
    return map;
  }

}

/// title : "x quiz"
/// description : "Test your basic product x knowledge"
/// subject : "Product x"
/// audiance : "engineers"
/// created : "2016-02-18T03:22:56.637Z"
/// updated : "2016-02-18T03:48:35.824Z"
/// tagList : ["basic","x"]

class Meta {
  String _title;
  String _description;
  String _subject;
  String _audiance;
  String _created;
  String _updated;
  List<String> _tagList;

  String get title => _title;
  String get description => _description;
  String get subject => _subject;
  String get audiance => _audiance;
  String get created => _created;
  String get updated => _updated;
  List<String> get tagList => _tagList;

  Meta({
      String title, 
      String description, 
      String subject, 
      String audiance, 
      String created, 
      String updated, 
      List<String> tagList}){
    _title = title;
    _description = description;
    _subject = subject;
    _audiance = audiance;
    _created = created;
    _updated = updated;
    _tagList = tagList;
}

  Meta.fromJson(dynamic json) {
    _title = json['title'];
    _description = json['description'];
    _subject = json['subject'];
    _audiance = json['audiance'];
    _created = json['created'];
    _updated = json['updated'];
    _tagList = json['tagList'] != null ? json['tagList'].cast<String>() : [];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['title'] = _title;
    map['description'] = _description;
    map['subject'] = _subject;
    map['audiance'] = _audiance;
    map['created'] = _created;
    map['updated'] = _updated;
    map['tagList'] = _tagList;
    return map;
  }

}