export class Page<T> {
    total: number;
    hasNext: boolean;
    items: T[];

    constructor(total: number, hasNext: boolean, items: T[]) {
        this.total = total;
        this.hasNext = hasNext;
        this.items = items;
    }
}
