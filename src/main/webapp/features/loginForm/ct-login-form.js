'use strict';

angular.module('catan')
        .directive('ctLoginForm', ['Auth', function (Auth) {
            return {
                restrict: 'E',
                scope: {},
                templateUrl: "/features/loginForm/ct-login-form.html",
                link: function (scope) {

                    scope.data = {};

                    scope.submit = function () {
                        Auth.login(scope.data.username, scope.data.password)
                                .then(function () {
                                    alert('Success login');
                                }, function (response) {
                                    alert('Error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                                });
                    };

                }
            };
        }]);