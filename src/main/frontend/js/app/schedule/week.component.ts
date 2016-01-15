import {Component, View} from "angular2/core";
import {Router, RouteParams} from "angular2/router";
import {Week} from "./week";
import {ScheduleService} from "./schedule.service";
import {PersonService} from "../person/person.service";
import {Person} from "../person/person";
import {Observable} from "rxjs/Rx";

@Component({})
@View({
    templateUrl: 'partial/schedule/week.html'
})
export class WeekComponent {
    week:Week = new Week();
    personName:string;
    people:Person[] = [];

    constructor(private personService:PersonService,
                private scheduleService:ScheduleService,
                private router:Router,
                private routeParams:RouteParams) {
        this.week.person = new Person(); // mock while loading data

        var week = routeParams.get('week');
        var year = routeParams.get('year');

        Observable.zip(
            this.personService.findAll()
                .flatMap(p => {
                    var total = p.total;
                    if (total <= p.items.length) {
                        return Observable.of(p.items);
                    }

                    var ratio = total * 1. / 25 /* check PersonService */;
                    var iterations = ratio == (ratio | 0) ? ratio : ratio;
                    return Observable.concat(
                        Observable.of(p.items),
                        Observable.range(1, iterations).concatMap(i => this.personService.findAll().map(p => p.items)));
                }),
            this.scheduleService.findByWeek(+week, +year))
                .subscribe(results => {
                    this.people = this.people.concat(results[0]);
                    if (this.week) {
                        this.week = results[1];
                    }
                });
    }

    onSubmit() {
        if (!this.personName || this.personName == this.week.person.name) {
            return;
        }
        this.scheduleService.updateWeek(this.week.week, this.week.year, this.personName)
            .subscribe(res => this.router.navigate(['Schedule', {year: this.week.year, week: this.week.week}]));
    }
}
