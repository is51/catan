import { User } from 'app/shared/domain/user';
import { Player } from 'app/shared/domain/player/player';
import { GameMap } from 'app/shared/domain/game-map/game-map';
import { Dice } from 'app/shared/domain/dice';

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

    private _onStartPlaying: Function;

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
        this.dice = new Dice(params.dice);

        if (params.map) {
            this.map = new GameMap(params.map);
        }
    }

    update(params) {
        //TODO: revise this method
        //this.gameId = params.gameId;

        this.creatorId = params.creatorId;
        this.privateGame = params.privateGame;
        this.privateCode = params.privateCode;
        this.dateCreated = params.dateCreated;
        this.maxPlayers = params.maxPlayers;
        this.minPlayers = params.minPlayers;
        this.targetVictoryPoints = params.targetVictoryPoints;

        this.dateStarted = params.dateStarted;
        this.currentMove = params.currentMove;
        this.biggestArmyOwnerId = params.biggestArmyOwnerId;
        this.longestWayOwnerId = params.longestWayOwnerId;

        params.gameUsers.forEach(playerParams => {
            let player = this.players.filter(player => player.id === playerParams.id)[0];
            if (player) {
                player.update(playerParams);
            } else {
                this.players.push(new Player(playerParams));
            }
        });
        for (let i = 0; i < this.players.length; i++) {
            let playerParams = params.gameUsers.filter(playerParams => playerParams.id === this.players[i].id)[0];
            if (!playerParams) {
                this.players.splice(i, 1);
                i--;
            }
        }

        this.dice.update(params.dice);

        if (params.map) {
            if (this.map) {
                this.map.update(params.map);
            } else {
                this.map = new GameMap(params.map);
            }
        } else {
            this.map = null;
        }



        if (this.status !== params.status) {
            this.status = params.status;

            if (this.isPlaying()) {
                this.triggerStartPlaying();
            }
        }

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

    getMovingPlayer() {
        return this.players.filter(player => player.moveOrder === this.currentMove)[0];
    }

    getPlayer(playerId: number) {
        return this.players.filter(player => player.id === playerId)[0];
    }



    //TODO: try to replace with Subscribable
    onStartPlaying(onStartPlaying: Function) {
        this._onStartPlaying = onStartPlaying;
    }
    cancelOnStartPlaying() {
        this._onStartPlaying = undefined;
    }
    triggerStartPlaying() {
        if (this._onStartPlaying) {
            this._onStartPlaying();
        }
    }

}