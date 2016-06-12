import { Injectable } from 'angular2/core';

const TOKEN_STORAGE_NAME = 'token';

@Injectable()
export class AuthTokenService {
    get() {
        return window.localStorage.getItem(TOKEN_STORAGE_NAME);
    }

    set(token: string) {
        window.localStorage.setItem(TOKEN_STORAGE_NAME, token);
    }

    delete() {
        window.localStorage.removeItem(TOKEN_STORAGE_NAME);
    }
}