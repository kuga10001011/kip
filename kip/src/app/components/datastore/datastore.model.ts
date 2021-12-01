import { Deserializable } from "../../shared/deserializable";
import { Row } from "./row/row.model";
import { Field } from "./field/field.model";

export class Datastore implements Deserializable {

  id?: number;
  name!: string;
  rows: Set<Row> = new Set<Row>();
  fields: Set<Field> = new Set<Field>();

  deserialize(input: any): this {
    Object.assign(this, input);
    if (input.rows) {
      this.rows = new Set(input.rows.map((row: any) => new Row().deserialize(row)));
    }
    if (input.fields) {
      this.fields = new Set(input.fields.map((field: any) => new Field().deserialize(field)));
    }
    return this;
  }

  toJSON() {
    return {
      id: this.id,
      name: this.name,
      rows: Array.from(this.rows),
      fields: Array.from(this.fields)
    }
  }


}
