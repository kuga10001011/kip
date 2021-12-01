import { Deserializable } from "../../shared/deserializable";
import { Row } from "./row/row.model";
import { Field } from "./field/field.model";
import { Value } from "./value/value.model";

export class FieldValue implements Deserializable {

  id?: number;
  field!: Field;
  value!: Value;

  deserialize(input: any): this {
    Object.assign(this, input);
    this.field = new Field().deserialize(input.field);
    this.value = new Value().deserialize(input.value);
    return this;
  }

  set(field: Field, value: any): this {
    this.field = field;
    this.value = value;
    return this;
  }

}
