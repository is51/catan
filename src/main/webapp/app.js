'use strict';

angular.module('catan', [
    'ngRoute',
    'ngCookies'
  ])

  .config(['$routeProvider', function($routeProvider) {   
    $routeProvider.when('/', {templateUrl: 'features/startPage/start-page.html', controller: 'StartPageController'});
    $routeProvider.otherwise({redirectTo: '/'});
  }])

  .config(['$httpProvider', function($httpProvider) {   
    $httpProvider.interceptors.push('AuthInterceptor');
  }]);