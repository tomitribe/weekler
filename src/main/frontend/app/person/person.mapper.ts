import {Person} from "./person";

export class PersonMapper {
    public static jsonToPerson(json: any) {
        var person = new Person();
        person.name = json.name;
        person.displayName = json.displayName;
        person.mail = json.mail;
        person.phone = json.phone;
        person.icon = json.icon;
        return person;
    }
}