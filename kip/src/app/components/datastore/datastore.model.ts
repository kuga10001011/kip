import { Deserializable } from "../../shared/deserializable";
import { Row } from "./row/row.model";
import { Field } from "./field/field.model";
import { View } from "./view/view.model";

export class Datastore implements Deserializable {

  id?: number;
  name!: string;
  rows: Set<Row> = new Set<Row>();
  fields: Set<Field> = new Set<Field>();
  views: Set<View> = new Set<View>();

  deserialize(input: any): this {
    Object.assign(this, input);
    if (input.rows) {
      this.rows = new Set(input.rows.map((row: any) => new Row().deserialize(row)));
    }
    if (input.fields) {
      this.fields = new Set(input.fields.map((field: any) => new Field().deserialize(field)));
    }
    if (input.views) {
      this.views = new Set(input.views.map((view: any) => new View().deserialize(view)));
    }
    return this;
  }

  toJSON() {
    return {
      id: this.id,
      name: this.name,
      rows: Array.from(this.rows),
      fields: Array.from(this.fields),
      views: Array.from(this.views)
    }
  }


}
