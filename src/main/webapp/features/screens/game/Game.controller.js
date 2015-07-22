'use strict';

angular.module('catan')
    .controller('GameController', ['$scope', '$stateParams', '$location', 'Remote', function($scope, $stateParams, $location, Remote) {

        $scope.gameDetails = null;

        $scope.updateGameDetails = function() {
            Remote.game.details({gameId: $stateParams.gameId})
                .then(function(response) {
                    $scope.gameDetails = response.data;
                }, function(response) {
                    alert('Getting Game Details Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                    $location.path("/");
                });
        };

        $scope.updateGameDetails();

    }]);