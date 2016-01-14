import {Component, View} from 'angular2/core';
import {NgForm}    from 'angular2/common';
import {RouteParams, Router, RouterLink} from "angular2/router";

import {Person} from "./person";
import {PersonService} from "./person.service";

@Component({})
@View({
    directives: [RouterLink],
    templateUrl: 'partial/person/person.html'
})
export class PersonComponent {
    person: Person = new Person();
    isUpdate: boolean = false;
    showSuccessMessage: boolean = false;
    message: string = '';

    constructor(private personService: PersonService,
                private routeParams: RouteParams,
                private router: Router) {
        var name = routeParams.get('name');
        if (name) {
            this.isUpdate = true;
            this.personService.findById(name).subscribe(p => this.person = p);
        }
    }

    onSubmit() {
        this.personService.save(this.person).subscribe(data => {
            this.person = data;
            this.showSuccessMessage = true;
            this.message = 'Person ' + (this.isUpdate ? 'Updated' : 'Created');
        });
    }

    onDelete() {
        this.personService.deleteById(this.person.name).subscribe(data => {
            this.person = data;
            this.showSuccessMessage = true;
            this.message = 'Person Deleted';
        })
    }

    hideSuccessBox() {
        this.showSuccessMessage = false;
    }
}
