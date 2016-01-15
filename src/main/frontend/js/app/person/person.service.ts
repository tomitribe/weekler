import {Person} from "./person";
import {PersonMapper} from "./person.mapper";
import {Page} from "../util/page";
import {Rest} from "../util/rest";
import {Http, Headers, Response} from 'angular2/http';
import {Injectable} from "angular2/core";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

@Injectable()
export class PersonService {
    constructor(private http: Http) {
    }

    save(person: Person): Observable<Person> {
        var obs: Observable<Response>;
        if (person.name) { // update
            obs = this.http.put('api/person', JSON.stringify(person), Rest.jsonHeaders());
        } else { // create
            obs = this.http.post('api/person', JSON.stringify(person), Rest.jsonHeaders());
        }
        return obs.map(res => PersonMapper.jsonToPerson(res.json()));
    }

    findById(name: string) {
        return this.http.get('api/person/' + name, Rest.jsonHeaders()).map(res => PersonMapper.jsonToPerson(res.json()));
    }

    findAll(page: number = 0): Observable<Page<Person>> {
        return this.http.get('api/person?page=' + page, Rest.jsonHeaders())
            .map(res => {
                var page = res.json();
                return new Page<Person>(page.total, page.hasNext, page.items.map(i => PersonMapper.jsonToPerson(i)));
            });
    }

    deleteById(name: string) {
        return this.http.delete('api/person/' + name, Rest.jsonHeaders()).map(res => PersonMapper.jsonToPerson(res.json()));
    }
}
