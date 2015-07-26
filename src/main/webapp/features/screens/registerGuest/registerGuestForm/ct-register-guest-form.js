'use strict';

angular.module('catan')
        .directive('ctRegisterGuestForm', ['Auth', '$state', '$stateParams', function(Auth, $state, $stateParams) {
            return {
                restrict: 'E',
                scope: {},
                templateUrl: "/features/screens/registerGuest/registerGuestForm/ct-register-guest-form.html",
                link: function(scope) {

                    scope.data = {};

                    scope.submit = function() {
                        Auth.registerAndLoginGuest(scope.data.username)
                            .then(function() {
                                if ($stateParams.onRegister) {
                                    $stateParams.onRegister();
                                }
                            }, function(response) {
                                alert('Registration guest error: ' + ((response.data.errorCode) ? response.data.errorCode : 'unknown'));
                            });
                    };

                }
            };
        }]);