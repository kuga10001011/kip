import { Deserializable } from "../../shared/deserializable";

export class Range implements Deserializable {

  floor: number;
  ceiling: number;

  constructor(floor: number, ceiling: number) {
    this.floor = floor;
    this.ceiling = ceiling;
  }

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

}
