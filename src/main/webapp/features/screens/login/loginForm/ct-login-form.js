'use strict';

angular.module('catan')
    .directive('ctLoginForm', ['Auth', '$state', function(Auth, $state) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/login/loginForm/ct-login-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Auth.login(scope.data.username, scope.data.password)
                        .then(function() {
                            $state.go('start');
                        }, function(response) {
                            alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);