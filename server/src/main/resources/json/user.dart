/// user : {"userId":123,"email":"test@test.com","token":"jwt.token.here","username":"tester","userNameSequence":1,"created":"2016-02-18T03:22:56.637Z","day":2,"week":"5","bio":"Test login api","image":"https://api.hello-avatar.com/adorables/avatars/285","securedLoan":100}

class User {
  User _user;

  User get user => _user;

  User({
      User user}){
    _user = user;
}

  User.fromJson(dynamic json) {
    _user = json['user'] != null ? User.fromJson(json['user']) : null;
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    if (_user != null) {
      map['user'] = _user.toJson();
    }
    return map;
  }

}

/// userId : 123
/// email : "test@test.com"
/// token : "jwt.token.here"
/// username : "tester"
/// userNameSequence : 1
/// created : "2016-02-18T03:22:56.637Z"
/// day : 2
/// week : "5"
/// bio : "Test login api"
/// image : "https://api.hello-avatar.com/adorables/avatars/285"
/// securedLoan : 100

class User {
  int _userId;
  String _email;
  String _token;
  String _username;
  int _userNameSequence;
  String _created;
  int _day;
  String _week;
  String _bio;
  String _image;
  int _securedLoan;

  int get userId => _userId;
  String get email => _email;
  String get token => _token;
  String get username => _username;
  int get userNameSequence => _userNameSequence;
  String get created => _created;
  int get day => _day;
  String get week => _week;
  String get bio => _bio;
  String get image => _image;
  int get securedLoan => _securedLoan;

  User({
      int userId, 
      String email, 
      String token, 
      String username, 
      int userNameSequence, 
      String created, 
      int day, 
      String week, 
      String bio, 
      String image, 
      int securedLoan}){
    _userId = userId;
    _email = email;
    _token = token;
    _username = username;
    _userNameSequence = userNameSequence;
    _created = created;
    _day = day;
    _week = week;
    _bio = bio;
    _image = image;
    _securedLoan = securedLoan;
}

  User.fromJson(dynamic json) {
    _userId = json['userId'];
    _email = json['email'];
    _token = json['token'];
    _username = json['username'];
    _userNameSequence = json['userNameSequence'];
    _created = json['created'];
    _day = json['day'];
    _week = json['week'];
    _bio = json['bio'];
    _image = json['image'];
    _securedLoan = json['securedLoan'];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['userId'] = _userId;
    map['email'] = _email;
    map['token'] = _token;
    map['username'] = _username;
    map['userNameSequence'] = _userNameSequence;
    map['created'] = _created;
    map['day'] = _day;
    map['week'] = _week;
    map['bio'] = _bio;
    map['image'] = _image;
    map['securedLoan'] = _securedLoan;
    return map;
  }

}