'use strict';

angular.module('catan')
  .service('Auth', ["$http", "$location", "$q", "$window", function ($http, $location, $q, $window) {
    var Auth = {
      getToken: function () {
        return $window.localStorage.getItem('token');
      },

      setToken: function (token) {
        $window.localStorage.setItem('token', token);
      },

      deleteToken: function () {
        $window.localStorage.removeItem('token');
      },

      login: function (username, password) {
        var deferred = $q.defer();

        $http.post('/dummy-response.json', {
          username: username, password: password
        }).success(function (response, status, headers, config) {
          if (response.token) {
            Auth.setToken(response.token);
          }

          deferred.resolve(response, status, headers, config);
        }).error(function (response, status, headers, config) {
          deferred.reject(response, status, headers, config);
        });

        return deferred.promise;
      },

      logout: function () {
        Auth.deleteToken();
        $window.location = '/';
      },

      register: function (user) {
        var deferred = $q.defer();

        $http.post('/api/v1/auth/register/', {
          user: user
        }).success(function (response, status, headers, config) {
          Auth.login(user.username, user.password).
            then(function (response, status, headers, config) {
              $window.location = '/';
            });

          deferred.resolve(response, status, headers, config);
        }).error(function (response, status, headers, config) {
          deferred.reject(response, status, headers, config);
        });

        return deferred.promise;
      }
    };

    return Auth;
  }]);