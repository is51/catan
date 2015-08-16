'use strict';

angular.module('catan')
    .directive('ctRegisterForm', ['Auth', '$state', '$stateParams', function(Auth, $state, $stateParams) {
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
                                    if ($stateParams.onRegister) {
                                        $stateParams.onRegister();
                                    } else {
                                        $state.go('start');
                                    }
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