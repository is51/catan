import { Component, OnInit } from 'angular2/core';
import { Game } from 'app/shared/domain/game';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

@Component({
    selector: 'ct-top-message',
    templateUrl: 'app/play/top-message/top-message.component.html',
    inputs: ['game']
})

export class TopMessageComponent implements OnInit {
    game: Game;
    text: string = null;

    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
        this.game.getCurrentPlayer(this._authUser.get())
            .onDisplayedMessageUpdate(
                text => this._show(text),
                () => this._hide()
            );
    }

    private _show(text: string) {
        this.text = text;
    }

    private _hide() {
        this.text = null;
    }
}