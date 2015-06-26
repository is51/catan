'use strict';

angular.module('catan')

  .directive('ctLoginForm', ['Auth', function(Auth) {
    return {
      restrict: 'E',
      templateUrl: "/features/loginForm/ct-login-form.html",
      link: function(scope, element, attrs) {

        scope.data = {};

        scope.submit = function() {

          Auth
            .login(scope.data.login, scope.data.password)
            .then(function(response) {
              alert('ok');
              console.log(response);
            }, function(response) {
              alert('fault');
              console.log(response);
            });

        };

      }
    };
  }]);