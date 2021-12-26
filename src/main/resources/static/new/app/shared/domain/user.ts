export class User {
    id: number;
    guest: boolean;
    username: string;
    firstName: string;
    lastName: string;

    constructor(params: any) {
        this.id = params.id;
        this.guest = params.guest;
        this.username = params.username;
        this.firstName = params.firstName;
        this.lastName = params.lastName;
    }

    update(params: any) {
        //TODO: revise this method
        this.guest = params.guest;
        this.username = params.username;
        this.firstName = params.firstName;
        this.lastName = params.lastName;
    }

    getDisplayedName() {
        return this.username;
    }
}