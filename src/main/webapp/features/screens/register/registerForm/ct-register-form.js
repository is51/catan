'use strict';

angular.module('catan')
    .directive('ctRegisterForm', ['Auth', '$state', function(Auth, $state) {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: "/features/screens/register/registerForm/ct-register-form.html",
            link: function(scope) {

                scope.data = {};

                scope.submit = function() {
                    Auth.register(scope.data.username, scope.data.password)
                        .then(function() {

                            Auth.login(scope.data.username, scope.data.password)
                                .then(function() {
                                    alert('Successful registration');
                                    $state.go('start');
                                }, function(response) {
                                    alert('Login error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });

                        }, function(response) {
                            alert('Registration error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                        });
                };

            }
        };
    }]);