import { User } from 'app/shared/interfaces/user';
import { AvailableActions } from './available-actions';

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

    constructor(params) {
        this.id = params.id;
        this.colorId = params.colorId;
        this.moveOrder = params.moveOrder;
        this.ready = params.ready;

        this.achievements = params.achievements;
        this.developmentCards = params.developmentCards;
        this.resources = params.resources;
        this.user = params.user;

        if (params.availableActions) {
            this.availableActions = new AvailableActions(params.availableActions);
        }
    }

    update(params) {
        //TODO: revise this method
        this.id = params.id;
        this.colorId = params.colorId;
        this.moveOrder = params.moveOrder;
        this.ready = params.ready;

        this.achievements = params.achievements;
        this.developmentCards = params.developmentCards;
        this.resources = params.resources;
        this.user = params.user;

        this.availableActions = (params.availableActions) ? new AvailableActions(params.availableActions) : undefined;
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

interface Resources {
    brick: number;
    wood: number;
    sheep: number;
    wheat: number;
    stone: number;
}