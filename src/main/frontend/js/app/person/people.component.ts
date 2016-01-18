import {Component, View} from "angular2/core";
import {RouterLink} from "angular2/router";
import {Observable} from "rxjs/Observable";
import {Page} from "../util/page";
import {Person} from "./person";
import {PersonService} from "./person.service";

@Component({})
@View({
    directives: [RouterLink],
    templateUrl: 'partial/person/people.html'
})
export class PeopleComponent {
    pageNumber: number = -1;
    page: Page<Person> = new Page<Person>(0, false, []);

    constructor(private personService: PersonService) {
        this.loadNext();
    }

    loadPrevious() {
        if (this.pageNumber <= 0) {
            return;
        }
        this.pageNumber--;
        this.personService.findAll(this.pageNumber).subscribe(page => this.page = page);
    }

    loadNext() {
        this.pageNumber++;
        this.personService.findAll(this.pageNumber).subscribe(page => this.page = page);
    }
}
