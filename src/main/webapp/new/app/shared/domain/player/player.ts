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