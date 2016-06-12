import { Component, OnInit } from 'angular2/core';

import { SelectService } from 'app/play/shared/services/select.service';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

import { Game } from 'app/shared/domain/game';
import { Resources } from 'app/shared/domain/player/resources';

//TODO: this directive should be refactored

@Component({
    selector: 'ct-choose-resources',
    templateUrl: 'app/play/shared/choose-resources/choose-resources.component.html',
    styleUrls: ['app/play/shared/choose-resources/choose-resources.component.css'],
    inputs: ['game', 'type']
})

export class ChooseResourcesComponent implements OnInit {
    game: Game;
    type: string;

    balanceType: string;
    resources: Resources = new Resources();

    private _playerResources: Resources;
    private _tradePortRatio: Resources;
    private _maxResourcesCountLimit: number;
    private _maxResourcesCountForApply: number;
    private _minResourcesCountLimit: number;
    private _minResourcesCountForApply: number;
    private _removeOtherResourceWhenLimit: boolean;
    private _tradeBalance: number = 0;
    
    constructor(
        private _select: SelectService,
        private _authUser: AuthUserService) { }
    
    ngOnInit() {
        let currentPlayer = this.game.getCurrentPlayer(this._authUser.get());

        this._playerResources = currentPlayer.resources;

        if (this.type === "TRADE_PORT") {
            this._tradePortRatio = new Resources(currentPlayer.availableActions.getParams("TRADE_PORT"));
        }

        this._maxResourcesCountLimit = this._getMaxResourcesCountLimit(this.type);
        this._maxResourcesCountForApply = this._getMaxResourcesCountForApply(this.type, this._playerResources);
        this._minResourcesCountLimit = this._getMinResourcesCountLimit(this.type, this._playerResources);
        this._minResourcesCountForApply = this._getMinResourcesCountForApply(this.type, this._playerResources);

        this._removeOtherResourceWhenLimit = this._getRemoveOtherResourceWhenLimit(this.type);

        this.balanceType = this._getBalanceType(this.type);
    }

    addResource(resourceType: string) {
        let step = 1;
        if (this.type === "TRADE_PORT" && this.resources[resourceType] < 0) {
            step = this._tradePortRatio[resourceType];
        }

        if (this.resources.getTotalCount() + step <= this._maxResourcesCountLimit || this._maxResourcesCountLimit === null) {
            this.resources[resourceType] += step;
        } else if (this._removeOtherResourceWhenLimit && step === 1) {
            if (this.removeOtherResource(resourceType)) {
                this.resources[resourceType]++;
            }
        }

        if (this.type === "TRADE_PORT") {
            this._tradeBalance = this._recalculateTradeBalance(this.resources, this._tradePortRatio);
        }
    }

    removeResource(resourceType: string) {
        let step = 1;
        if (this.type === "TRADE_PORT" && this.resources[resourceType] <= 0) {
            step = this._tradePortRatio[resourceType];
        }

        if (this.resources.getTotalCount() - step >= this._minResourcesCountLimit &&
            this._playerResources[resourceType] - step + this.resources[resourceType] >= 0) {
            this.resources[resourceType] -= step;
        }

        if (this.type === "TRADE_PORT") {
            this._tradeBalance = this._recalculateTradeBalance(this.resources, this._tradePortRatio);
        }
    }

    removeOtherResource(resourceType: string) {
        for (let i in this.resources) {
            if (i !== resourceType && this.resources[i] > 0) {
                this.resources[i]--;
                return true;
            }
        }
        return false;
    }

    okDisabled() {
        let count = this.resources.getTotalCount();

        if (this.type === "TRADE_PROPOSE" && this._noNegativeOrPositiveResources(this.resources)) {
            return true;
        }

        return this.resources.areAllCountsZero()
            || count < this._minResourcesCountForApply
            || (count > this._maxResourcesCountForApply && this._maxResourcesCountForApply !== null)
            || this._tradeBalance !== 0;
    }

    cancel() {
        this._select.cancelRequestSelection(this.type);
    }

    ok() {
        this._select.select(this.type, this._convertResourcesToSelection(this.type, this.resources));
    }

    private _convertResourcesToSelection(type: string, resources: Resources) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                let firstResource;
                let secondResource;
                for (let i in resources) {
                    if (resources[i] > 0) {
                        firstResource = i.toUpperCase();
                        resources[i]--;
                        break;
                    }
                }
                for (let i in resources) {
                    if (resources[i] > 0) {
                        secondResource = i.toUpperCase();
                    }
                }
                return {
                    firstResource,
                    secondResource
                };
            case "CARD_MONOPOLY":
                for (let i in resources) {
                    if (resources[i] > 0) {
                        return {resource: i.toUpperCase()};
                    }
                }
                return;
            case "KICK_OFF_RESOURCES":
                return {
                    brick: -resources.brick,
                    wood: -resources.wood,
                    sheep: -resources.sheep,
                    wheat: -resources.wheat,
                    stone: -resources.stone
                };
            case "TRADE_PORT":
            case "TRADE_PROPOSE":
                return {
                    brick: resources.brick,
                    wood: resources.wood,
                    sheep: resources.sheep,
                    wheat: resources.wheat,
                    stone: resources.stone
                };
        }
    }

    private _getMaxResourcesCountLimit(type: string) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return 2;
            case "CARD_MONOPOLY":
                return 1;
            case "KICK_OFF_RESOURCES":
                return 0;
            case "TRADE_PORT":
                return null; //unlimited
            case "TRADE_PROPOSE":
                return null; //unlimited
        }
    }

    private _getMaxResourcesCountForApply(type: string, playerResources: Resources) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return 2;
            case "CARD_MONOPOLY":
                return 1;
            case "KICK_OFF_RESOURCES":
                return -this._calculateCountForKickOff(playerResources);
            case "TRADE_PORT":
                return null; //unlimited
            case "TRADE_PROPOSE":
                return null; //unlimited
        }
    }

    private _getMinResourcesCountLimit(type: string, playerResources: Resources) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return 0;
            case "CARD_MONOPOLY":
                return 0;
            case "KICK_OFF_RESOURCES":
                return -this._calculateCountForKickOff(playerResources);
            case "TRADE_PORT":
                return -playerResources.getTotalCount();
            case "TRADE_PROPOSE":
                return -playerResources.getTotalCount();
        }
    }

    private _getMinResourcesCountForApply(type: string, playerResources: Resources) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return 2;
            case "CARD_MONOPOLY":
                return 1;
            case "KICK_OFF_RESOURCES":
                return -this._calculateCountForKickOff(playerResources);
            case "TRADE_PORT":
                return -playerResources.getTotalCount();
            case "TRADE_PROPOSE":
                return -playerResources.getTotalCount();
        }
    }

    private _getRemoveOtherResourceWhenLimit(type: string) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return true; //TODO: or 'false' for 'year of plenty'?
            case "CARD_MONOPOLY":
                return true;
            case "KICK_OFF_RESOURCES":
                return false;
            case "TRADE_PORT":
                return false;
            case "TRADE_PROPOSE":
                return false;
        }
    }

    private _getBalanceType(type: string) {
        switch (type) {
            case "CARD_YEAR_OF_PLENTY":
                return "POSITIVE";
            case "CARD_MONOPOLY":
                return "POSITIVE";
            case "KICK_OFF_RESOURCES":
                return "NEGATIVE";
            case "TRADE_PORT":
                return "BOTH";
            case "TRADE_PROPOSE":
                return "BOTH";
        }
    }

    private _calculateCountForKickOff(resources: Resources) {
        return Math.floor(resources.getTotalCount() / 2);
    }

    private _recalculateTradeBalance(resources: Resources, tradePortRatio: Resources) {
        let tradeBalance = 0;
        for (let i in resources) {
            let count = resources[i];
            let ratio = tradePortRatio[i];
            if (count > 0) {
                tradeBalance += count;
            }
            if (count < 0) {
                tradeBalance += count / ratio;
            }
        }
        return tradeBalance;
    }

    private _noNegativeOrPositiveResources(resources: Resources) {
        let isNegative = false;
        let isPositive = false;

        for (let i in resources) {
            if (resources[i] > 0) {
                isPositive = true;
            }
            if (resources[i] < 0) {
                isNegative = true;
            }
        }
        return !isPositive || !isNegative;
    }

}