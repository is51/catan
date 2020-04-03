System.register(['angular2/core', './shared/services/play.service', './shared/services/select.service', './shared/services/marking.service', './shared/services/execute-actions.service', 'app/shared/services/auth/auth-user.service', 'app/shared/services/notification/notification.service', './shared/services/templates.service', './resources-panel/resources-panel.component', './players-panel/players-panel.component', './actions-panel/actions-panel.component', './buy-panel/buy-panel.component', './game-map/game-map.component', './cards-panel/cards-panel.component', './trade-panel/trade-panel.component', './trade-reply-panel/trade-reply-panel.component', './card-year-of-plenty-choose-resources-panel/card-year-of-plenty-choose-resources-panel.component', './card-monopoly-choose-resource-panel/card-monopoly-choose-resource-panel.component', './kick-off-resources-panel/kick-off-resources-panel.component', './dice/dice.component', './top-message/top-message.component', './game-results/game-results.component', './log-panel/log-panel.component', './log-button/log-button.component'], function(exports_1, context_1) {
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
    var core_1, play_service_1, select_service_1, marking_service_1, execute_actions_service_1, auth_user_service_1, notification_service_1, templates_service_1, resources_panel_component_1, players_panel_component_1, actions_panel_component_1, buy_panel_component_1, game_map_component_1, cards_panel_component_1, trade_panel_component_1, trade_reply_panel_component_1, card_year_of_plenty_choose_resources_panel_component_1, card_monopoly_choose_resource_panel_component_1, kick_off_resources_panel_component_1, dice_component_1, top_message_component_1, game_results_component_1, log_panel_component_1, log_button_component_1;
    var PlayComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (play_service_1_1) {
                play_service_1 = play_service_1_1;
            },
            function (select_service_1_1) {
                select_service_1 = select_service_1_1;
            },
            function (marking_service_1_1) {
                marking_service_1 = marking_service_1_1;
            },
            function (execute_actions_service_1_1) {
                execute_actions_service_1 = execute_actions_service_1_1;
            },
            function (auth_user_service_1_1) {
                auth_user_service_1 = auth_user_service_1_1;
            },
            function (notification_service_1_1) {
                notification_service_1 = notification_service_1_1;
            },
            function (templates_service_1_1) {
                templates_service_1 = templates_service_1_1;
            },
            function (resources_panel_component_1_1) {
                resources_panel_component_1 = resources_panel_component_1_1;
            },
            function (players_panel_component_1_1) {
                players_panel_component_1 = players_panel_component_1_1;
            },
            function (actions_panel_component_1_1) {
                actions_panel_component_1 = actions_panel_component_1_1;
            },
            function (buy_panel_component_1_1) {
                buy_panel_component_1 = buy_panel_component_1_1;
            },
            function (game_map_component_1_1) {
                game_map_component_1 = game_map_component_1_1;
            },
            function (cards_panel_component_1_1) {
                cards_panel_component_1 = cards_panel_component_1_1;
            },
            function (trade_panel_component_1_1) {
                trade_panel_component_1 = trade_panel_component_1_1;
            },
            function (trade_reply_panel_component_1_1) {
                trade_reply_panel_component_1 = trade_reply_panel_component_1_1;
            },
            function (card_year_of_plenty_choose_resources_panel_component_1_1) {
                card_year_of_plenty_choose_resources_panel_component_1 = card_year_of_plenty_choose_resources_panel_component_1_1;
            },
            function (card_monopoly_choose_resource_panel_component_1_1) {
                card_monopoly_choose_resource_panel_component_1 = card_monopoly_choose_resource_panel_component_1_1;
            },
            function (kick_off_resources_panel_component_1_1) {
                kick_off_resources_panel_component_1 = kick_off_resources_panel_component_1_1;
            },
            function (dice_component_1_1) {
                dice_component_1 = dice_component_1_1;
            },
            function (top_message_component_1_1) {
                top_message_component_1 = top_message_component_1_1;
            },
            function (game_results_component_1_1) {
                game_results_component_1 = game_results_component_1_1;
            },
            function (log_panel_component_1_1) {
                log_panel_component_1 = log_panel_component_1_1;
            },
            function (log_button_component_1_1) {
                log_button_component_1 = log_button_component_1_1;
            }],
        execute: function() {
            //TODO: revise pointer-events of all map elements (set 'none' most of it)
            PlayComponent = (function () {
                function PlayComponent(_notification, _authUser, _actions, _templates) {
                    this._notification = _notification;
                    this._authUser = _authUser;
                    this._actions = _actions;
                    this._templates = _templates;
                    this.templatesLoaded = false;
                }
                PlayComponent.prototype.ngOnInit = function () {
                    var _this = this;
                    this.availableActions = this.game.getCurrentPlayer(this._authUser.get()).availableActions;
                    this._checkIfImmediateAndExecute();
                    this.availableActions.onUpdate(function (newActions) {
                        _this._checkIfHasNotificationAndNotify(newActions);
                        _this._checkIfImmediateAndExecute();
                    });
                    this._templates.load()
                        .then(function () {
                        _this.templatesLoaded = true;
                    });
                };
                PlayComponent.prototype.ngOnDestroy = function () {
                    this.availableActions.cancelOnUpdate();
                };
                PlayComponent.prototype._checkIfHasNotificationAndNotify = function (actions) {
                    var _this = this;
                    actions.forEach(function (action) {
                        if (action.notify) {
                            _this._notification.notifyGlobal(action.notifyMessage, action.code);
                        }
                    });
                };
                PlayComponent.prototype._checkIfImmediateAndExecute = function () {
                    if (this.availableActions.isImmediate) {
                        this._actions.execute(this.availableActions.list[0].code, this.game);
                    }
                };
                PlayComponent = __decorate([
                    core_1.Component({
                        selector: 'ct-play',
                        templateUrl: 'app/play/play.component.html',
                        directives: [
                            resources_panel_component_1.ResourcesPanelComponent,
                            players_panel_component_1.PlayersPanelComponent,
                            actions_panel_component_1.ActionsPanelComponent,
                            buy_panel_component_1.BuyPanelComponent,
                            game_map_component_1.GameMapComponent,
                            cards_panel_component_1.CardsPanelComponent,
                            trade_panel_component_1.TradePanelComponent,
                            trade_reply_panel_component_1.TradeReplyPanelComponent,
                            card_year_of_plenty_choose_resources_panel_component_1.CardYearOfPlentyChooseResourcesPanelComponent,
                            card_monopoly_choose_resource_panel_component_1.CardMonopolyChooseResourcePanelComponent,
                            kick_off_resources_panel_component_1.KickOffResourcesPanelComponent,
                            dice_component_1.DiceComponent,
                            top_message_component_1.TopMessageComponent,
                            game_results_component_1.GameResultsComponent,
                            log_panel_component_1.LogPanelComponent,
                            log_button_component_1.LogButtonComponent
                        ],
                        providers: [
                            play_service_1.PlayService,
                            select_service_1.SelectService,
                            execute_actions_service_1.ExecuteActionsService,
                            marking_service_1.MarkingService,
                            templates_service_1.TemplatesService
                        ],
                        inputs: ['game']
                    }), 
                    __metadata('design:paramtypes', [(typeof (_a = typeof notification_service_1.NotificationService !== 'undefined' && notification_service_1.NotificationService) === 'function' && _a) || Object, (typeof (_b = typeof auth_user_service_1.AuthUserService !== 'undefined' && auth_user_service_1.AuthUserService) === 'function' && _b) || Object, execute_actions_service_1.ExecuteActionsService, templates_service_1.TemplatesService])
                ], PlayComponent);
                return PlayComponent;
                var _a, _b;
            }());
            exports_1("PlayComponent", PlayComponent);
        }
    }
});
