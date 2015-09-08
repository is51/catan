(function () {
    angular.module('shop', ['shop-http', 'shop-games']);

    angular.module('shop')
            .controller("StoreController", ["communicator", function (communicator) {
                this.games = [];
                this.populate = function (array) {
                    this.games = array;
                };
                var populateMethodWithoutLostContext = this.populate.bind(this);
                communicator.getAllGames(populateMethodWithoutLostContext, this);
            }]);

    angular.module('shop')
            .directive("gameTitle", function () {
                return {
                    restrict: 'E',
                    templateUrl: "game-title.html"
                };
            });

    /* var gamesList = [{
     id: 0,
     name: "IT-Catan",
     description: "Super puper game!!!! The best game you ever played.",
     price: 100,
     developers: ["ryzhenkovskiy", "petrovich", "syrovenko"],
     images: [
     {
     big: "catan_big_1.jpg",
     small: "catan_small1.jpg"
     }, {
     big: "catan_big_2.png",
     small: "catan_small2.jpg"
     }, {
     big: "catan_big_3.jpg",
     small: "catan_small3.jpg"
     }],
     reviews: [
     {
     stars: 5,
     text: "Best game in the world",
     author: "me"
     }, {
     stars: 5,
     text: "I like it",
     author: "me"
     }]
     }, {
     id: 1,
     name: "GTA 6",
     description: "Fucken shit game!!!! The worst game you ever played.",
     price: 14.25,
     developers: ["ryzhenkovskiy", "petrovich", "syrovenko"],
     images: [
     {
     big: "gta_big_1.jpg",
     small: "gta_small1.jpg"
     }, {
     big: "gta_big_2.jpg",
     small: "gta_small2.jpg"
     }, {
     big: "gta_big_3.jpg",
     small: "gta_small3.jpg"
     }],
     reviews: [
     {
     stars: 2,
     text: "Some shit",
     author: "Andrey"
     }, {
     stars: 1,
     text: "I  don't like it",
     author: "me"
     }]
     }];*/


})();