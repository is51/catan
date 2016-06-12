import { User } from 'app/shared/domain/user';
import { AvailableActions } from './available-actions';
import { Resources } from './resources';

export class Player {
    id: number;
    colorId: number;
    moveOrder: number;
    ready: boolean;

    achievements: Achievements;
    developmentCards: DevelopmentCards;
    resources: Resources;
    user: User;

    availableActions: AvailableActions;

    displayedMessage: string;

    log: Log[];

    private _onShowDisplayedMessage: Function;
    private _onHideDisplayedMessage: Function;
    private _onDisplayedLogUpdate: Function;

    constructor(params) {
        this.id = params.id;
        this.colorId = params.colorId;
        this.moveOrder = params.moveOrder;
        this.ready = params.ready;

        this.achievements = <Achievements>params.achievements;
        this.developmentCards = <DevelopmentCards>params.developmentCards;
        this.resources = new Resources(params.resources);
        this.user = new User(params.user);

        if (params.availableActions) {
            this.availableActions = new AvailableActions(params.availableActions);
        }

        if (params.displayedMessage) {
            this.displayedMessage = params.displayedMessage;
            this.triggerDisplayedMessageShow(this.displayedMessage);
        }

        this.log = <Log[]>params.log;
    }

    update(params) {
        //TODO: revise this method
        //this.id = params.id;
        this.colorId = params.colorId;
        this.moveOrder = params.moveOrder;
        this.ready = params.ready;

        this.achievements = <Achievements>params.achievements;
        this.developmentCards = <DevelopmentCards>params.developmentCards;
        this.resources.update(params.resources);
        this.user.update(params.user);

        if (params.availableActions) {
            if (this.availableActions) {
                this.availableActions.update(params.availableActions);
            } else {
                this.availableActions = new AvailableActions(params.availableActions);
            }
        } else {
            this.availableActions = undefined;
        }

        if (params.displayedMessage && (!this.displayedMessage || this.displayedMessage !== params.displayedMessage)) {
            this.triggerDisplayedMessageShow(params.displayedMessage);
        } else if (this.displayedMessage && !params.displayedMessage) {
            this.triggerDisplayedMessageHide();
        }
        this.displayedMessage = params.displayedMessage;

        if (params.log) {
            let lastLogItem = (this.log) ? this.log[0] : null;
            this.log = <Log[]>params.log;
            let newLogItems = this._getNewDisplayedLogItems(lastLogItem);
            if (newLogItems) {
                this.triggerDisplayedLogUpdate(newLogItems);
            }
        }
    }

    //TODO: try to replace with Subscribable (it's used in game-page.component)
    onDisplayedMessageUpdate(onShowDisplayedMessage: Function, onHideDisplayedMessage: Function) {
        this._onShowDisplayedMessage = onShowDisplayedMessage;
        this._onHideDisplayedMessage = onHideDisplayedMessage;
    }
    cancelOnDisplayedMessageUpdate() {
        this._onShowDisplayedMessage = undefined;
        this._onHideDisplayedMessage = undefined;
    }
    triggerDisplayedMessageShow(text: string) {
        if (this._onShowDisplayedMessage) {
            this._onShowDisplayedMessage(text);
        }
    }
    triggerDisplayedMessageHide() {
        if (this._onHideDisplayedMessage) {
            this._onHideDisplayedMessage();
        }
    }

    onDisplayedLogUpdate(onLogUpdate: Function) {
        this._onDisplayedLogUpdate = onLogUpdate;
    }
    cancelOnDisplayedLogUpdate() {
        this._onDisplayedLogUpdate = undefined;
    }
    triggerDisplayedLogUpdate(newLogItems: Log[]) {
        if (this._onDisplayedLogUpdate) {
            this._onDisplayedLogUpdate(newLogItems);
        }
    }

    private _getNewDisplayedLogItems(lastLogItem: Log) {
        let newLogItems = <Log[]>[];
        for (let logItem of this.log) {
            if (lastLogItem && lastLogItem.id !== logItem.id) {
                if (logItem.displayedOnTop) {
                    newLogItems.push(logItem);
                }
            } else {
                break;
            }
        }
        return (newLogItems.length) ? newLogItems.reverse() : null;
    }
}

interface Achievements {
    displayVictoryPoints: number;
    longestWayLength: number;
    realVictoryPoints: number;
    totalCards: number;
    totalResources: number;
    totalUsedKnights: number;
}

interface DevelopmentCards {
    knight: number;
    monopoly: number;
    roadBuilding: number;
    victoryPoint: number;
    yearOfPlenty: number;
}

interface Log {
    id: number;
    date: number;
    code: string;
    message: string;
    displayedOnTop: boolean;
}