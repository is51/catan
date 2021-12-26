System.register(['angular2/core'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1;
    var PlayersListComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            PlayersListComponent = (function () {
                function PlayersListComponent() {
                }
                PlayersListComponent.prototype.ngDoCheck = function () {
                    //TODO: not optimal (needs to be updated only on changes)
                    this._calculateVacantPlaces();
                };
                PlayersListComponent.prototype._calculateVacantPlaces = function () {
                    var vacantPlacesCount = this.game.maxPlayers - this.game.players.length;
                    this.vacantPlaces = new Array(vacantPlacesCount);
                };
                PlayersListComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-players-list',
                        templateUrl: 'app/menu/shared/players-list/players-list.component.html',
                        styleUrls: ['app/menu/shared/players-list/players-list.component.css'],
                        inputs: ['game', 'showReadyStatus']
                    }), 
                    __metadata('design:paramtypes', [])
                ], PlayersListComponent);
                return PlayersListComponent;
            }());
            exports_1("PlayersListComponent", PlayersListComponent);
        }
    }
});
