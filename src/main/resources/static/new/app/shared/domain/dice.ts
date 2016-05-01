export class Dice {
    thrown: boolean;
    first: number;
    second: number;
    value: number;

    private _onThrow: Function;

    constructor(params) {
        if (!params) {
            return;
        }

        this.thrown = params.thrown;
        this.first = params.first;
        this.second = params.second;
        this.value = params.value;
    }

    update(params) {
        if (!params) {
            return;
        }

        // TODO: add dice.id (number) to indicate if new dice thrown

        if (this.thrown !== params.thrown ||
            this.first !== params.first ||
            this.second !== params.second) {

            this.thrown = params.thrown;
            this.first = params.first;
            this.second = params.second;
            this.value = params.value;

            if (this.thrown) {
                this.triggerThrow(this.value);
            }
        }
    }

    //TODO: try to replace with Subscribable
    onThrow(_onThrow: Function) {
        this._onThrow = _onThrow;
    }
    cancelOnThrow() {
        this._onThrow = undefined;
    }
    triggerThrow(value) {
        if (this._onThrow) {
            this._onThrow(value);
        }
    }
}