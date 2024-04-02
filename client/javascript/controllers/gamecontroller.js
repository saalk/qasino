angular.module('myApp')
        .directive('myGameDirective', function() {
            return {
                restrict: 'AE',
                templateUrl: function(elem, attrs) {
                    return attrs.templatePath;
                }
            };
        })
        .controller('GameController', ['$scope', 'cardgameService','toastr', 'envService',
function ($scope, cardgameService, toastr){

    /* jshint validthis: true */
    var vm = this;
    vm.cardGame;
    vm.players;
    vm.defaultAi = {alias: "Alien Doe", avatar: "Elf", human: "false", aiLevel: "None", cubits: 0, securedLoan: 0};
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

    // default RRANNN ante=n/h betting=r/d deck=a/h/c/s/d/r insurance=n/q/h round=n/1 turns=n/1/2/3
    vm.variants = [{text:'Default - Double or nothing, multiple rounds, no winner', type:'RDAN1N'},
    {text:'Todo - Highest bet wins, only 1 round', type:'HRAN1N'},
    {text:'Todo - Double or nothing,  Highest bet wins, hearts only', type:'HDHNNN'}];
    vm.myVariant = vm.variants[0];

    vm.antes = [{amount:'20'},
    {amount:'50'},
    {amount:'100'},
    {amount:'200'},
    {amount:'500'}];
    vm.myAnte = vm.antes[0];

    // make sure there are only 2 aliens
    initAliens(2);

    // ---
    // PUBLIC VIEW BEHAVIOUR METHODS
    // ---
    vm.changeAlien = function (index) {

        initAliens(2);
        loopAiLevel(index);
        if (vm.players[index].visitor.aiLevel.toUpperCase() == 'NONE') {
            vm.players[index].visitor.securedLoan = 0;
        } else {
            vm.players[index].visitor.securedLoan = (Math.ceil(Math.random() * 500)+ 500);
        };
        cardgameService.changeVisitorDetailsForGame( vm.cardGame, vm.players[index] )
            .then( applyRemoteData, function( errorMessage ) {
                toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };
    vm.prepareForShuffle = function () {

        vm.updateGameDetails();
        // find ai players having none as ai level -> delete them
        // TODO delete only the extra/specific aliens when less/one is/are needed
        for (i=0; i < vm.players.length; i++) {
           if (vm.players[i].visitor.aiLevel.toUpperCase() == 'NONE') {
               cardgameService.deleteAiPlayerForGame( vm.cardGame, vm.players[i] )
               .then( applyRemoteData, function( errorMessage ) {
                   toastr.error('Removing one alien failed: ' + errorMessage, 'Error');
                   }
               );
           }
        };

    };
    // ---
    // PUBLIC API METHODS
    // ---
    // update the given game with the type and ante.
    vm.updateGameDetails = function() {
        vm.cardGame.gameType = 'Hi-Lo'
        vm.cardGame.gameVariant = vm.myVariant.type;
        vm.cardGame.ante = vm.myAnte.amount;
        cardgameService.updateGame( vm.cardGame )
            .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };
    // process the add-player
    vm.setupAiPlayerForGame = function( functionCardGame, ai) {
        // If the data we provide is invalid, the promise will be rejected,
        // at which point we can tell the user that something went wrong. In
        // this case, toastr is used
        cardgameService.setupAiPlayerForGame( functionCardGame, ai )
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
    vm.deleteAiPlayerForGame = function( functionCardGame, ai ) {
        // Rather than doing anything clever on the client-side, I'm just
        // going to reload the remote data.
        cardgameService.deleteAiPlayerForGame( functionCardGame, ai )
            .then( applyRemoteData, function( errorMessage ) {
                    toastr.error('An error has occurred:' + errorMessage, 'Error');
                }
            )
        ;
    };

    // ---
    // PRIVATE METHODS USED IN PUBLIC METHODS OR TO INIT THE PAGE
    // ---

    // proceed to the next aiLevel for the player at the index
    function loopAiLevel(index) {
        if (vm.players[index].visitor.aiLevel.toUpperCase() == 'NONE') {
            if (vm.players[1].visitor.aiLevel.toUpperCase() == 'NONE' && index === 2) {
                vm.players[index].visitor.aiLevel = 'None';
                //vm.players[index].label = vm.none;
            } else {
                vm.players[index].visitor.aiLevel = 'Dumb';
                //vm.players[index].label = vm.dumb;
            };
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'DUMB') {
            vm.players[index].visitor.aiLevel = 'Medium';
            //vm.players[index].label = vm.average;
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'MEDIUM') {
            vm.players[index].visitor.aiLevel = 'Smart';
            //vm.players[index].label = vm.smart;
        } else if (vm.players[index].visitor.aiLevel.toUpperCase() == 'SMART') {
            if (vm.players[2].visitor.aiLevel.toUpperCase() !== 'NONE' && index === 1) {
                vm.players[index].visitor.aiLevel = 'Dumb';
                //vm.players[index].label = vm.dumb;
            } else {
                vm.players[index].visitor.aiLevel = 'None';
                //vm.players[index].label = vm.none;
            };
        };
    };

    function initAliens( needed ) {
        // always get the cardgame from the service in this init
        vm.cardGame = cardgameService.getGameStoredInService();
        vm.players = vm.cardGame.players;

        //set the ante
        if (vm.cardGame.ante === 0) {
            vm.cardGame.gameType = 'Hi-Lo'
            vm.cardGame.gameVariant = vm.myVariant.type;
            vm.cardGame.ante = vm.myAnte.amount;
            cardgameService.updateGame( vm.cardGame )
                .then( applyRemoteData, function( errorMessage ) {
                        toastr.error('An error has occurred:' + errorMessage, 'Error');
                    }
                )
            ;
        };

        // count the aliens
        count = 0;
        for (i=0; i < vm.players.length; i++) {
             if (vm.players[i].visitor.human === 'false') {
                 count++;
            }
        };
        toastr.info('There are ' + count + ' alien player(s) and ' + needed + ' is/are needed.', 'Info');

        if (needed < count ) {
            // more humans than needed -> delete all Aliens
            // TODO delete only the extra/specific aliens when less/one is/are needed
             for (i=0; i < vm.players.length; i++) {
                if (vm.players[i].visitor.human === 'false') {
                    cardgameService.deleteAiPlayerForGame( vm.cardGame, vm.players[i] )
                    .then( applyRemoteData, function( errorMessage ) {
                        toastr.error('Removing one alien failed: ' + errorMessage, 'Error');
                        }
                    );
                }
            };
            for (i = 0 ; i < needed; i++) {
                vm.defaultAi.alias = vm.defaultAi.alias + i;
                // add one or more aliens until needed
                cardgameService.setupAiPlayerForGame( vm.cardGame, vm.defaultAi )
                       .then( applyRemoteData, function( errorMessage ) {
                           toastr.error('Initializing new alien failed: ' + errorMessage, 'Error');
                       }
                   );
            }
        } else if (needed > count) {
            // no aliens or too few? -> keep adding one until ok
            extra = needed - count;
            for (i = 0 ; i < extra; i++) {
                // add one or more aliens
                cardgameService.setupAiPlayerForGame( vm.cardGame, vm.defaultAi )
                       .then( applyRemoteData, function( errorMessage ) {
                           toastr.error('Initializing new alien failed: ' + errorMessage, 'Error');
                       }
                   );
            }
        }
    };
    // apply the remote data to the local scope.
    function applyRemoteData( responseCardGame ) {

        vm.cardGame = responseCardGame;
        vm.players = responseCardGame.players;

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