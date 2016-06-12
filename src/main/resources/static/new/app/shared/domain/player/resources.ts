export class Resources {
    brick: number = 0;
    wood: number = 0;
    sheep: number = 0;
    wheat: number = 0;
    stone: number = 0;

    constructor(params?) {
        if (params) {
            if (params.brick != undefined) this.brick = params.brick;
            if (params.wood != undefined) this.wood = params.wood;
            if (params.sheep != undefined) this.sheep = params.sheep;
            if (params.wheat != undefined) this.wheat = params.wheat;
            if (params.stone != undefined) this.stone = params.stone;
        }
    }

    update(params) {
        if (params) {
            if (params.brick != undefined) this.brick = params.brick;
            if (params.wood != undefined) this.wood = params.wood;
            if (params.sheep != undefined) this.sheep = params.sheep;
            if (params.wheat != undefined) this.wheat = params.wheat;
            if (params.stone != undefined) this.stone = params.stone;
        }
    }

    getTotalCount() {
        return this.brick +
            this.wood +
            this.sheep +
            this.wheat +
            this.stone;
    }

    areAllCountsZero() {
        return this.brick === 0 &&
            this.wood === 0 &&
            this.sheep === 0 &&
            this.wheat === 0 &&
            this.stone === 0;
    }
}