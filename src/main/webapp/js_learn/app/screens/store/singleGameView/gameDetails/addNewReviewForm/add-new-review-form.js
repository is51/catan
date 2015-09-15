(function () {

    angular.module('shop-games')
            .directive("addNewReviewForm", function () {
                return {
                    restrict: 'E',
                    templateUrl: "app/screens/store/singleGameView/gameDetails/addNewReviewForm/add-new-review-form.html",
                    controllerAs: "reviewCtrl",
                    controller: function () {
                        this.review = {};
                        this.addReview = function (game) {
                            game.reviews.push(this.review);
                            this.review = {};
                        }
                    }

                };
            });

})();