(function () {

    angular.module('shop-games')
            .directive("imageGallery", function () {
                return {
                    restrict: 'E',
                    templateUrl: "app/directives/imageGallery/image-gallery.html",
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