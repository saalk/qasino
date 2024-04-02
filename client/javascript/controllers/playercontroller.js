angular.module('myApp')
        .directive('myPlayerDirective', function() {
            return {
                restrict: 'AE',
                templateUrl: function(elem, attrs) {
                    return attrs.templatePath;
                }
            };
        })
        .controller('PlayerController', ['$scope', 'cardgameService', 'toastr', 'envService',
function ($scope, cardgameService, toastr){

    /* jshint validthis: true */
    var vm = this;
    vm.cardGame = {};
    vm.players;
    vm.name = 'Visitor';
    // flags for ng-if and check if player details are ok
    vm.showListForDebug = true;
    vm.gotogame = false;
    // init a game and human = true player only when not present in service
    init('true', vm.name);

    // ---
    // PUBLIC VIEW BEHAVIOUR METHODS
    // ---
    vm.setHumanName = function () {
        //toastr.clear();
        vm.players[0].visitor.alias = 'John Doe';
        toastr.info('Your name is set', 'Information');
        cardgameService.changeVisitorDetailsForGame( vm.cardGame, vm.players[0] )
               .then( applyRemoteData, function( errorMessage ) {
                toastr.error('Setting name failed: ' + errorMessage, 'Error');
                }
         );
    };

    vm.pawnHumanShipForCubits = function (extraCubits) {
        minimum = 0 + extraCubits;
        if (vm.players[0].visitor.securedLoan === 0 ) {
            vm.players[0].visitor.securedLoan = (Math.ceil(Math.random() * (1000 - minimum))+ minimum);
            //vm.players[0].visitor.cubits = vm.players[0].visitor.cubits + vm.players[0].visitor.securedLoan;
            // toastr.info('<body>Your ship is pawned for at least {{ vm.minimum }}<body>', 'InformationL',{allowHtml: true});
            toastr.info('Your ship is pawned', 'Information');
        } else if (vm.players[0].visitor.cubits < vm.players[0].visitor.securedLoan) {
            toastr.error('Your don\'t have not enough credits', 'Error');
        } else if (vm.players[0].visitor.cubits >= vm.players[0].visitor.securedLoan) {
            //vm.players[0].visitor.cubits = vm.players[0].visitor.cubits - vm.players[0].visitor.securedLoan;
            vm.players[0].visitor.securedLoan = 0;
            toastr.info('Your loan is repayed', 'Information');
            vm.gotogame = false;
        };
        cardgameService.changeVisitorDetailsForGame( vm.cardGame, vm.players[0] )
               .then( applyRemoteData, function( errorMessage ) {
                toastr.error('Setting pawn failed: ' + errorMessage, 'Error');
                }
         );
    };

    // ---
    // PRIVATE METHODS USED IN PUBLIC BEHAVIOUR METHODS + INIT
    // ---
    function init( human, name ) {
        // always get the cardgame from the service
        vm.cardGame = cardgameService.getGameStoredInService();
        if (vm.cardGame.gameId == null || angular.isUndefined(vm.cardGame.gameId)) {
            toastr.info('Initializing game for new visitor.', 'Informational');
            // add a new game and visitor and relate the visitor to the game
            cardgameService.initGameForVisitor( human , name )
                   .then( applyRemoteData, function( errorMessage ) {
                        toastr.error('Initializing new player failed: ' + errorMessage, 'Error');
                    }
                )
            ;
        } else {
        // there is a player so setup a new game with this player
            vm.cardGame = cardgameService.getGameStoredInService();
            vm.players = vm.cardGame.players;
            cardgameService.initGameForExistingVisitor( vm.cardGame , vm.players )
                               .then( applyRemoteData, function( errorMessage ) {
                                    toastr.error('Initializing new game for player failed: ' + errorMessage, 'Error');
                                }
                            )
                        ;

            toastr.info('Start a new game for existing player.' + vm.cardGame, 'Informational');
        };
    };

    // ---
    // PUBLIC API METHODS
    // ---
    // update the given player supplied
    vm.changeVisitorDetailsForGame = function( functionCardGame, player ) {

        // Rather than doing anything clever on the client-side, I'm just
        // going to reload the remote data.
        cardgameService.changeVisitorDetailsForGame( functionCardGame, player )
               .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('Updating player failed: ' + errorMessage, 'Error');
                }
            )
        ;
    };  

    // ---
    // PRIVATE METHODS USED IN PUBLIC API METHODS OR TO INIT THE PAGE
    // ---
    // apply the remote data to the local scope.
    function applyRemoteData( responseCardGame ) {

        vm.cardGame = responseCardGame;
        vm.players = responseCardGame.players;
        vm.name = vm.players[0].visitor.alias;

        vm.gotogame = false;
        if ((vm.players[0].visitor.securedLoan !== 0) && (vm.players[0].visitor.alias.toUpperCase() !== 'VISITOR' )) {
            vm.gotogame = true;
        };
        if (vm.players[0].visitor.alias.toUpperCase() == 'VISITOR' && vm.players[0].visitor.cubits !== 0) {
            setTimeout(function() {
            toastr.warning('Get your name for the casino, '+ vm.name, 'Warning');},1000);
        } else if (vm.players[0].visitor.cubits === 0 && vm.players[0].visitor.alias.toUpperCase() !== 'VISITOR') {
            setTimeout(function() {
            toastr.warning('Pawn your ship for the casino', 'Warning');},1000);
        };

    };
}]);
