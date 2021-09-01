/// tags : ["basic","x","y"]

class Tags {
  List<String> _tags;

  List<String> get tags => _tags;

  Tags({
      List<String> tags}){
    _tags = tags;
}

  Tags.fromJson(dynamic json) {
    _tags = json['tags'] != null ? json['tags'].cast<String>() : [];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map['tags'] = _tags;
    return map;
  }

}