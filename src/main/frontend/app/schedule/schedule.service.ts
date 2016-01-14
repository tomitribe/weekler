import {Week} from "./week";
import {PersonMapper} from "../person/person.mapper";
import {Person} from "../person/person";
import {Rest} from "../util/rest";
import {Http, Headers, Response} from 'angular2/http';
import {Injectable} from "angular2/core";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/filter';

@Injectable()
export class ScheduleService {
    constructor(private http:Http) {
    }

    findByWeek(week:number = -1, year: number = -1): Observable<Week> {
        return this.http.get('api/schedule/week?week=' + week + '&year=' + year, Rest.jsonHeaders())
            .filter(res => res.status == 200 /* 204 means empty, skip */).map(res => ScheduleService.jsonToWeek(res.json()));
    }

    updateWeek(week:number, year:number, person:string): Observable<Week> {
        var json = new Week();
        json.week = week;
        json.year = year;
        json.person = new Person();
        json.person.name = person;

        return this.http.put('api/schedule/week', JSON.stringify(json), Rest.jsonHeaders())
            .map(res => ScheduleService.jsonToWeek(res.json()));
    }

    private static jsonToWeek(json:any) {
        if (json && json.person) {
            var week = new Week();
            week.week = json.week;
            week.year = json.year;
            week.person = PersonMapper.jsonToPerson(json.person);
            return week;
        }
        return null;
    }
}
