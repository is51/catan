import { bootstrap } from 'angular2/platform/browser'

import { Component, provide } from 'angular2/core';
import { ROUTER_PROVIDERS, LocationStrategy, HashLocationStrategy } from 'angular2/router';

import { AppComponent } from 'app/app.component'

bootstrap(AppComponent, [
    ROUTER_PROVIDERS,
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]);