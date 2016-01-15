import {Component} from 'angular2/core';
import {RouteConfig, Router, ROUTER_DIRECTIVES} from 'angular2/router';

import {PeopleComponent} from './person/people.component';
import {PersonComponent} from "./person/person.component";
import {ScheduleComponent} from "./schedule/schedule.component";
import {WeekComponent} from "./schedule/week.component";

@Component({
    selector: 'weekler',
    template: `<router-outlet></router-outlet>`,
    directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
    {
        name: 'People',
        path: '/people',
        component: PeopleComponent
    },
    {
        name: 'PersonCreate',
        path: '/person',
        component: PersonComponent
    },
    {
        name: 'PersonEdit',
        path: '/person/:name',
        component: PersonComponent
    },
    {
        name: 'Week',
        path: '/week/:year/:week',
        component: WeekComponent
    },
    {
        name: 'Schedule',
        path: '/schedule/:year/:week',
        component: ScheduleComponent
    },
    {
        name: 'DefaultSchedule',
        path: '/schedule',
        component: ScheduleComponent,
        useAsDefault: true
    }
])
export class RouterComponent {}
