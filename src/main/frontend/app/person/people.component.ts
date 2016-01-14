import {Component, View} from "angular2/core";
import {RouterLink} from "angular2/router";
import {Page} from "../util/page";
import {Person} from "./person";
import {PersonService} from "./person.service";

@Component({})
@View({
    directives: [RouterLink],
    templateUrl: 'partial/person/people.html'
})
export class PeopleComponent {
    page: Page<Person> = new Page<Person>(0, false, []);

    constructor(private personService: PersonService) {
        personService.findAll(0).subscribe(page => this.page = page);
    }
}
