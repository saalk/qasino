angular.module('myApp')
       .service('cardgameService', ['$http', '$q', 'toastr', '$httpParamSerializerJQLike', 'envService',
function ($http, $q, toastr, $httpParamSerializerJQLike, envService){

         envService.set('production');
         // interface
         var cardGame = {},
         service = {

            getGameStoredInService: getGameStoredInService,
            getGameDetails: getGameDetails,

            initGameForVisitor: initGameForVisitor,
            initGameForExistingVisitor: initGameForExistingVisitor,

            updateGame: updateGame,

            setupAiPlayerForGame: setupAiPlayerForGame,
            changeVisitorDetailsForGame: changeVisitorDetailsForGame,
            deleteAiPlayerForGame: deleteAiPlayerForGame,

            shuffleGame: shuffleGame,
            turnGame: turnGame

         };
         return service;

    // implementations
    function getGameStoredInService() {
    //
        return cardGame;
    }
    function getGameDetails( suppliedCardGame ) {

        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        // then() returns a new promise. We return that new promise.
        // that new promise is resolved via response.data,
        // i.e. the game in the private method handleSuccess
        var request = $http({
                method: 'get',
                crossDomain: true,
                url: apiUrl +'/cardgames/' + suppliedCardGame.gameId ,
                headers: {'Content-Type': 'application/json'}
//                params: {
//                    action: 'get'
//                }
            });
        return( request.then( handleSuccess, handleError ) );
    }
    function initGameForVisitor( human, name ) {

        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        if (human==='true') {
           humanOrAi = 'human';
        } else {
           humanOrAi = 'ai';

        }
        var request = $http({
            method: 'post',
            crossDomain: true,
            url: apiUrl +'/cardgames/init/' + humanOrAi
            + '?alias=' + name
            + '&avatar=Elf&aiLevel=Human&securedLoan=0',
            headers: {'Content-Type': 'application/json'},            //           params: {
            //               action: 'add'
            //           },
            data: '{}'
        });
        return( request.then( handleSuccess, handleError ) );
        
    }
    function initGameForExistingVisitor( suppliedCardGame, visitor ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'post',
            crossDomain: true,
            url: apiUrl +'/cardgames/init/human/' + visitor.visitorId
            + '?gameType=' + suppliedCardGame.gameType
            + '&gameVariant=' + suppliedCardGame.gameVariant
            + '&ante=' + suppliedCardGame.ante,
            headers: {'Content-Type': 'application/json'},            //           params: {
            //               action: 'add'
            //           },
            data: '{}'
        });
        return( request.then( handleSuccess, handleError ) );
    }
    function updateGame( suppliedCardGame ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'put',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/init'
            + '?gameType=' + suppliedCardGame.gameType
            + '&gameVariant=' + suppliedCardGame.gameVariant
            + '&ante=' + suppliedCardGame.ante,
            headers: {'Content-Type': 'application/json'},
            data: '{}'
            //
            //            params: {
//                action: 'update'
//            },
        });
        return( request.then( handleSuccess, handleError ) );
    }
    function setupAiPlayerForGame( suppliedCardGame, ai ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'post',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/setup/ai'
            + '?alias=' + ai.alias
            + '&avatar='  + ai.avatar
            + '&aiLevel='  + ai.aiLevel
            + '&securedLoan='  + ai.securedLoan ,
            headers: {'Content-Type': 'application/json'},            //           params: {
            //               action: 'add'
            //           },
            data: '{}'
       });
       return( request.then( handleSuccess, handleError ) );
    }
    function changeVisitorDetailsForGame( suppliedCardGame, player ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'put',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/setup/players/' + player.playerId
            + '?alias=' + player.visitor.alias
            + '&avatar=Elf'
            + '&aiLevel='  + player.visitor.aiLevel
            + '&securedLoan='  + player.visitor.securedLoan ,
            headers: {'Content-Type': 'application/json'},
            //
            //            params: {
//                action: 'update'
//            },
           data: player.visitor
        });
        return( request.then( handleSuccess, handleError ) );
    }
    function deleteAiPlayerForGame( suppliedCardGame, player ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'delete',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/setup/ai/' + player.playerId,
            headers: {'Content-Type': 'application/json'}
        });
        return( request.then( handleSuccess, handleError ) );
    }
    function shuffleGame( suppliedCardGame, jokers ) {
       apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

       var request = $http({
            method: 'post',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/shuffle/cards'
            + '?jokers=' + jokers
            + '&test=false',
            headers: {'Content-Type': 'application/json'},            //           params: {
            //               action: 'add'
            //           },
            data: '{}'
       });
       return( request.then( handleSuccess, handleError ) );
    }
    function turnGame( suppliedCardGame, action ) {
        apiUrl = envService.read('apiUrl'); // gets '//localhost/api' or '//knikit.nl/api'

        var request = $http({
            method: 'put',
            crossDomain: true,
            url: apiUrl +'/cardgames/' + suppliedCardGame.gameId + '/turn/players/' + suppliedCardGame.currentPlayerId
            + '?action=' + action
            + '&total=1&cardLocation=Hand' ,
            headers: {'Content-Type': 'application/json'},            //           params: {
            //               action: 'add'
            //           },
            data: '{}'
       });
       return( request.then( handleSuccess, handleError ) );
    }

    // PRIVATE METHODS.
    // transform the error response, unwrapping the application dta from
    // the API response payload.
    function handleError( response ) {
        // The API response from the server should be returned in a
        // nomralized format. However, if the request was not handled by the
        // server (or what not handles properly - ex. server error), then we
        // may have to normalize it on our end, as best we can.
        if (
            ! angular.isObject( response.data ) ||
            ! response.data.message
            ) {
            return( $q.reject( 'An unknown error occurred.' ) );
        }
        // Otherwise, use expected error message.
        return( $q.reject( response.data.message ) );
    }
    // transform the successful response, unwrapping the application data
    // from the API response payload.
    function handleSuccess( response ) {
        cardGame = (response.data.cardGame);
        return cardGame;
    }
}]);
