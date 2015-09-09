(function () {
    var app = angular.module('shop-games', []);

    app.directive("gameDetailsTabs", function(){
        return {
            restrict: 'E',
            templateUrl: "game-details-tabs.html",
            controllerAs: "tab",
            controller: function () {
                this.active = 1;

                this.setActive = function (value) {
                    this.active = value;
                };

                this.isActive = function (tab) {
                    return this.active === tab;
                };
            }
        };
    });

    app.directive("addNewReviewForm", function(){
        return {
            restrict: 'E',
            templateUrl: "add-new-review-form.html",
            controllerAs: "reviewCtrl",
            controller: function () {
                this.review = {};
                this.addReview = function(game){
                    game.reviews.push(this.review);
                    this.review = {};
                }
            }

        };
    });

    app.directive("imageGallery", function(){
        return {
            restrict: 'E',
            templateUrl: "image-gallery.html",
            controllerAs: "gallery",
            controller: function () {
                this.current = 0;
                this.setCurrent = function (value) {
                    this.current = value;
                }
            }
        };
    });
})();