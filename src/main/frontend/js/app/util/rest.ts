import {Headers} from "angular2/http";

export class Rest {
    public static jsonHeaders() {
        var headers = new Headers();
        headers.append('Content-Type', 'application/json');
        return { headers: headers };
    }
}