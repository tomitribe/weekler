import {Component, View} from "angular2/core";
import {RouterLink, RouteParams, Router} from "angular2/router";
import {Week} from "./week";
import {ScheduleService} from "./schedule.service";
import {Person} from "../person/person";
import {Observable} from "rxjs/Observable";
import 'rxjs/operator/filter';

@Component({})
@View({
    directives: [RouterLink],
    templateUrl: 'partial/schedule/schedule.html'
})
export class ScheduleComponent {
    week:Week = new Week();
    information: string;
    showInformation: boolean = false;

    constructor(private scheduleService:ScheduleService,
                private routeParams:RouteParams,
                private router: Router) {
        var weekParam = routeParams.get('week');
        var yearParam = routeParams.get('year');
        this.week.week = weekParam? +weekParam : -1;
        this.week.year = yearParam? +yearParam : -1;
        this.week.person = new Person(); // mock while loading data
        this.findWeek(this.week.week, this.week.year);
    }

    previousWeek() {
        if (this.week.week < 0) { // can happen if we go too far in the past (before the app started to work)
            return;
        }
        var firstWeek = this.week.week == 1;
        this.router.navigate(['Schedule', {year: firstWeek ? this.week.year - 1 : this.week.year, week: firstWeek ? ScheduleComponent.lastWeek(this.week.year - 1) : this.week.week - 1}]);
    }

    nextWeek() {
        var lastWeek = this.week.week == ScheduleComponent.lastWeek(this.week.year);
        this.router.navigate(['Schedule', {year: lastWeek ? this.week.year + 1 : this.week.year, week: lastWeek ? 1 : (this.week.week + +1)}]);
    }

    reaffectNextWeeks() {
        this.scheduleService.reaffectFrom(this.week.week, this.week.year)
            .filter(res => res.status < 300)
            .subscribe(ignored => {
                this.information = "Rescheduling done.";
                this.showInformation = true;
            });
    }

    private findWeek(week:number = -1, year:number = -1) {
        this.scheduleService.findByWeek(week, year).filter(w => w != null).subscribe(week => this.week = week);
    }

    private static lastWeek(year:number):number {
        var firstDay = new Date(year, 0, 1);
        return Math.ceil((((new Date(year, 11 /*december*/, 31).getTime() - firstDay.getTime()) / 86400000) + firstDay.getDay() + 1) / 7);
    }
}
