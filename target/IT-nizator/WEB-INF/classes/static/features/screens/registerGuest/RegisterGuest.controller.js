'use strict';

angular.module('catan')
        .controller('RegisterGuestController', ['$scope', '$stateParams', '$state', function($scope, $stateParams, $state) {

            $scope.login = function() {
                $state.go('login', {
                    onLogin: $stateParams.onRegister,
                    onBack: $stateParams.onBack
                });
            };

            $scope.registerRegularUser = function() {
                $state.go('register', {
                    onRegister: $stateParams.onRegister,
                    onBack: $stateParams.onBack
                });
            };

        }]);