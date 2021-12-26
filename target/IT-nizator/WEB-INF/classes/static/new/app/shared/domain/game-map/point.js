System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Point;
    return {
        setters:[],
        execute: function() {
            Point = (function () {
                function Point(x, y) {
                    this.x = x;
                    this.y = y;
                }
                Point.prototype.plus = function (point) {
                    return new Point(this.x + point.x, this.y + point.y);
                };
                Point.prototype.minus = function (point) {
                    return new Point(this.x - point.x, this.y - point.y);
                };
                Point.prototype.average = function (point) {
                    return new Point((this.x + point.x) / 2, (this.y + point.y) / 2);
                };
                return Point;
            }());
            exports_1("Point", Point);
        }
    }
});
