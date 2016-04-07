import { Component, OnInit, OnDestroy } from 'angular2/core';
import { Game } from 'app/shared/domain/game';
import { AuthUserService } from 'app/shared/services/auth/auth-user.service';

@Component({
    selector: 'ct-top-message',
    templateUrl: 'app/play/top-message/top-message.component.html',
    styleUrls: ['app/play/top-message/top-message.component.css'],
    inputs: ['game']
})

export class TopMessageComponent implements OnInit, OnDestroy {
    game: Game;
    text: string = null;
    boxWidth: number = 0;
    boxMaxWidth: number = 700;

    constructor(private _authUser: AuthUserService) { }

    ngOnInit() {
        let currentPlayer = this.game.getCurrentPlayer(this._authUser.get());

        if (currentPlayer.displayedMessage) {
            this._show(currentPlayer.displayedMessage);
        }

        currentPlayer.onDisplayedMessageUpdate(
                text => this._show(text),
                () => this._hide()
            );
    }

    private _show(text: string) {
        this.text = text;
        this.boxWidth = text.length * 20;
        if (this.boxWidth > this.boxMaxWidth) {
            this.boxWidth = this.boxMaxWidth;
        }
    }

    private _hide() {
        this.text = null;
    }

    ngOnDestroy() {
        this.game.getCurrentPlayer(this._authUser.get())
            .cancelOnDisplayedMessageUpdate();
    }
}