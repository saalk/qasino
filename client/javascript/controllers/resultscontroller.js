angular.module('myApp')
        .directive('myResultsDirective', function() {
            return {
                restrict: 'AE',
                templateUrl: function(elem, attrs) {
                    return attrs.templatePath;
                }
            };
        })
        .controller('ResultsController', ['$scope', 'cardgameService', 'toastr', 'envService',
function ($scope, cardgameService, toastr){

    // viewmodel for this controller
    var vm = this;
    vm.cardGame;
    vm.players;
    // flags + checks for ng-if
    vm.showListForDebug = true;
    vm.showalien1 = true;
    vm.showalien2 = true;
    vm.tothecasino = false;

    // fixed text
    vm.smart = 'Most evolved alien species, this fellow starts with ';
    vm.average = 'A nice competitor, he has a budget of ';
    vm.dumb = 'Quick to beat, starting with ';
    vm.none = 'This alien has left the game with ';
    
    // load players
    initResults();
    
    // ---
    // PUBLIC VIEW BEHAVIOUR METHODS 
    // ---
    vm.changeAlien = function (index) {
        loopAiLevel(index);
        if (vm.players[index].visitor.securedLoan === 0) {
             vm.players[index].visitor.cubits = (Math.ceil(Math.random() * 500)+ 500);
            vm.players[index].visitor.securedLoan = vm.players[index].visitor.cubits;
        };
        cardgameService.changeVisitorDetailsForGame( vm.players[index] )
            .then( applyRemoteData, function( errorMessage ) {
                toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };
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
    // PRIVATE METHODS USED IN PUBLIC BEHAVIOUR METHODS
    // ---

    function initResults() {
        vm.cardGame = cardgameService.getGameStoredInService();
        vm.players = vm.cardGame.players;


        if (vm.cardGame.state.toUpperCase() == 'TURN_ENDED') {
            // make a winner or just end the turn
        toastr.info('To do declare a winner and do the math..', 'Info');
        } ;
    };

    // proceed to the next aiLevel for the player at the index
    // TODO a copy of the gamecontroller
    function loopAiLevel(index) {
        if (vm.players[index].visitor.aiLevel.toUpperCase() == 'NONE') {
            if (vm.players[1].visitor.aiLevel.toUpperCase() == 'NONE' && index === 2) {
                vm.players[index].visitor.aiLevel = 'None';
                vm.players[index].label = vm.none;
            } else {
                vm.players[index].visitor.aiLevel = 'Dumb';
                vm.players[index].label = vm.dumb;
            };
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'DUMB') {
            vm.players[index].visitor.aiLevel = 'Medium';
            vm.players[index].label = vm.average;
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'MEDIUM') {
            vm.players[index].visitor.aiLevel = 'Smart';
            vm.players[index].label = vm.smart;
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'SMART') {
            if (vm.players[2].visitor.aiLevel.toUpperCase() !== 'NONE' && index === 1) {
                vm.players[index].visitor.aiLevel = 'Dumb';
                vm.players[index].label = vm.dumb;
            } else {
                vm.players[index].visitor.aiLevel = 'None';
                vm.players[index].label = vm.none;
            };
        };
    };
    // ---
    // PUBLIC API METHODS
    // ---
    // process the add-player
    vm.setupAiPlayerForGame = function(player) {
        // If the data we provide is invalid, the promise will be rejected,
        // at which point we can tell the user that something went wrong. In
        // this case, toastr is used
        cardgameService.setupAiPlayerForGame( player )
            .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };
    // update the given player from the current collection.
    vm.changeVisitorDetailsForGame = function( functionCardGame, player ) {
        // Rather than doing anything clever on the client-side, I'm just
        // going to reload the remote data.
        cardgameService.changeVisitorDetailsForGame( functionCardGame, player )
            .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };  
    // remove the given friend from the current collection.
    vm.deleteAiPlayerForGame = function( player ) {
        // Rather than doing anything clever on the client-side, I'm just
        // going to reload the remote data.
        cardgameService.deleteAiPlayerForGame( player.playerId )
            .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };  
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
        vm.tothecasino = true;
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
    };
}]);
