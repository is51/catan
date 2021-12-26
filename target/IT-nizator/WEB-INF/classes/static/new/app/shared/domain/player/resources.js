System.register([], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var Resources;
    return {
        setters:[],
        execute: function() {
            Resources = (function () {
                function Resources(params) {
                    this.brick = 0;
                    this.wood = 0;
                    this.sheep = 0;
                    this.wheat = 0;
                    this.stone = 0;
                    if (params) {
                        if (params.brick != undefined)
                            this.brick = params.brick;
                        if (params.wood != undefined)
                            this.wood = params.wood;
                        if (params.sheep != undefined)
                            this.sheep = params.sheep;
                        if (params.wheat != undefined)
                            this.wheat = params.wheat;
                        if (params.stone != undefined)
                            this.stone = params.stone;
                    }
                }
                Resources.prototype.update = function (params) {
                    if (params) {
                        if (params.brick != undefined)
                            this.brick = params.brick;
                        if (params.wood != undefined)
                            this.wood = params.wood;
                        if (params.sheep != undefined)
                            this.sheep = params.sheep;
                        if (params.wheat != undefined)
                            this.wheat = params.wheat;
                        if (params.stone != undefined)
                            this.stone = params.stone;
                    }
                };
                Resources.prototype.getTotalCount = function () {
                    return this.brick +
                        this.wood +
                        this.sheep +
                        this.wheat +
                        this.stone;
                };
                Resources.prototype.areAllCountsZero = function () {
                    return this.brick === 0 &&
                        this.wood === 0 &&
                        this.sheep === 0 &&
                        this.wheat === 0 &&
                        this.stone === 0;
                };
                return Resources;
            }());
            exports_1("Resources", Resources);
        }
    }
});
