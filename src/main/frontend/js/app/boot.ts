import {bootstrap} from 'angular2/platform/browser';
import {provide, enableProdMode} from "angular2/core";
import {ROUTER_PROVIDERS} from 'angular2/router';
import {HTTP_PROVIDERS} from "angular2/http";

import {RouterComponent} from './router.component';
import {PersonService} from "./person/person.service";
import {ScheduleService} from "./schedule/schedule.service";

enableProdMode();
bootstrap(RouterComponent, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    PersonService,
    ScheduleService
]);
