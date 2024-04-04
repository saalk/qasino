angular.module('myApp')
        .directive('myCasinoDirective', function() {
            return {
                restrict: 'AE',
                templateUrl: function(elem, attrs) {
                    return attrs.templatePath;
                }
            };
        })
        .controller('CasinoController', ['$scope', 'cardgameService', 'toastr', 'envService',
function ($scope, cardgameService, toastr){

    // viewmodel for this controller
    var vm = this;
    vm.cardGame;
    vm.players;
    vm.hand;
    vm.balance;
    // flags + checks for ng-if
    vm.showListForDebug = true;
    vm.higher = false;
    vm.lower = false;
    vm.deal = false;
    vm.pass = false;
    vm.next = false;


    vm.loopplayer = 0;
    vm.showalien1 = false;
    vm.showalien2 = false;

    initCasino();
 
    // ---
    // PUBLIC VIEW BEHAVIOUR METHODS 
    // ---
    vm.turn = function( action ) {
        toastr.info('Hold on 10 sec, processing for the turn the game..', 'Info');
        cardgameService.turnGame( vm.cardGame, action )
            .then( applyRemoteData, function( errorMessage ) {
                toastr.error('Action ' + action + ' for player ' + vm.cardGame.currentPlayerId + ' failed.' + errorMessage, 'Error');
                }
            )
        ;
    };

    // ---
    // PRIVATE METHODS USED IN PUBLIC BEHAVIOUR METHODS
    // ---
    function initCasino() {
        vm.cardGame = cardgameService.getGameStoredInService();
        vm.players = vm.cardGame.players;

        if (vm.cardGame.state.toUpperCase() == 'HAS_HUMAN') {
            cardgameService.shuffleGame( vm.cardGame, 1 )
                .then( applyRemoteData, function( errorMessage ) {
                        toastr.error('An error has occurred:' + errorMessage, 'Error');
                    }
                )
            ;
        toastr.info('Hold on 10 sec, shuffling the game with 1 joker..', 'Info');
        } ;
    };

    // ---
    // PUBLIC API METHODS
    // ---


    // ---
    // PRIVATE METHODS USED IN PUBLIC API METHODS AND INIT
    // ---
    // apply the remote data to the local scope.
    function applyRemoteData( responseCardGame ) {

        vm.cardGame = responseCardGame;
        vm.players = responseCardGame.players;

        if (vm.cardGame.currentPlayerId == 0) {
            vm.loopplayer = 0;
        } else {
           for (i=0, len = vm.players.length; i < len -1; i++) {
                if (vm.players[i].playerId == vm.cardGame.currentPlayerId ) {
                   vm.loopplayer = i;
                }
           };
        };

        vm.showalien1 = true;
        vm.showalien2 = true;

        for (i=0, len = vm.players.length; i < len -1; i++) {
            if (vm.players[i].visitor.aiLevel.toUpperCase() == 'NONE') {
                vm.tothecasino = false;
            };
            if (i === 1 && vm.players[1].visitor.aiLevel.toUpperCase() == 'NONE') {
                vm.showalien1 = false;
            };
            if (i === 2 && vm.players[2].visitor.aiLevel.toUpperCase() == 'NONE') {
                vm.showalien2 = false;
            };
        };

        vm.higher = false;
        vm.lower = false;
        vm.deal = false;
        vm.pass = false;
        vm.next = false;
        switch(vm.cardGame.state.toUpperCase()) {
            case 'IS_SHUFFLED':
                vm.deal = true;
                break;
            case 'PLAYING':
                vm.higher = true;
                vm.lower = true;
                vm.pass = true;
                break;
            case 'TURN_STARTED':
                vm.higher = true;
                vm.lower = true;
                vm.pass = false;
                break;
            case 'TURN_ENDED':
                vm.deal = true;
                break;
            default:
                break;

        };
    };
}]);
