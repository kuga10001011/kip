import { Deserializable } from "../../../shared/deserializable";

export class Value implements Deserializable {

  id?: number;
  value?: any;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  set(value: any): this {
    this.value = value;
    return this;
  }

}
