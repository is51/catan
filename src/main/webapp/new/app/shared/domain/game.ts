import { User } from 'app/shared/interfaces/user';
import { Player } from 'app/shared/domain/player/player';
import { GameMap } from 'app/shared/domain/game-map/game-map';

export class Game {
    gameId: number;

    creatorId: number;
    privateGame: boolean;
    privateCode: string;
    dateCreated: number;
    maxPlayers: number;
    minPlayers: number;
    targetVictoryPoints: number;

    status: string;
    dateStarted: number;
    currentMove: number;
    biggestArmyOwnerId: number;
    longestWayOwnerId: number;

    players: Player[];
    dice: Dice;
    map: GameMap;

    constructor(params) {
        this.gameId = params.gameId;

        this.creatorId = params.creatorId;
        this.privateGame = params.privateGame;
        this.privateCode = params.privateCode;
        this.dateCreated = params.dateCreated;
        this.maxPlayers = params.maxPlayers;
        this.minPlayers = params.minPlayers;
        this.targetVictoryPoints = params.targetVictoryPoints;

        this.status = params.status;
        this.dateStarted = params.dateStarted;
        this.currentMove = params.currentMove;
        this.biggestArmyOwnerId = params.biggestArmyOwnerId;
        this.longestWayOwnerId = params.longestWayOwnerId;

        this.players = params.gameUsers.map(playerParams => new Player(playerParams));
        this.dice = <Dice>params.dice;
        this.map = new GameMap(params.map);
    }

    update(params) {
        //TODO: revise this method
        this.gameId = params.gameId;

        this.creatorId = params.creatorId;
        this.privateGame = params.privateGame;
        this.privateCode = params.privateCode;
        this.dateCreated = params.dateCreated;
        this.maxPlayers = params.maxPlayers;
        this.minPlayers = params.minPlayers;
        this.targetVictoryPoints = params.targetVictoryPoints;

        this.status = params.status;
        this.dateStarted = params.dateStarted;
        this.currentMove = params.currentMove;
        this.biggestArmyOwnerId = params.biggestArmyOwnerId;
        this.longestWayOwnerId = params.longestWayOwnerId;

        this.players.forEach((player, key) => {
            player.update(params.gameUsers[key]);
        });

        this.dice = <Dice>params.dice;
        this.map.update(params.map);
    }

    getId() {
        return this.gameId;
    }

    isNew() {
        return this.status === "NEW";
    }

    isPlaying() {
        return this.status === "PLAYING";
    }

    isFinished() {
        return this.status === "FINISHED";
    }

    getCurrentPlayer(currentUser: User) {
        return this.players.filter(player => player.user.id === currentUser.id)[0];
    }

    getPlayer(playerId: number) {
        return this.players.filter(player => player.id === playerId)[0];
    }

}

interface Dice {
    thrown: boolean;
    first: number;
    second: number;
    value: number;
}