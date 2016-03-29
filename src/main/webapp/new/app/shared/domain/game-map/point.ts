export class Point {
    x: number;
    y: number;

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    plus(point: Point) {
        return new Point(
            this.x + point.x,
            this.y + point.y
        );
    }

    minus(point: Point) {
        return new Point(
            this.x - point.x,
            this.y - point.y
        );
    }

    average(point: Point) {
        return new Point(
            (this.x + point.x) / 2,
            (this.y + point.y) / 2
        );
    }
}