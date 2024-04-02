// filters for generic purpose
angular
    .module('myApp')
    .filter('minLength', function(){
        return function(input, len, pad){
            if (input === undefined) { input = 0; };
            input = input.toString(); 
            if(input.length >= len) return input;
            else{
              pad = (pad || 0).toString(); 
              return new Array(1 + len - input.length).join(pad) + input;
            }
    };
});