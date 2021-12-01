import { Deserializable } from "../../../shared/deserializable";
import { FieldValue } from "../field-value.model";

export class Row implements Deserializable {

  id?: number;
  fieldValues: Set<FieldValue> = new Set<FieldValue>();

  deserialize(input: any): this {
    Object.assign(this, input);
    if (input.fieldValues) {
      this.fieldValues = new Set(input.fieldValues.map((fieldValue: any) => new FieldValue().deserialize(fieldValue)));
    }
    return this;
  }

  toJSON() {
    return {
      id: this.id,
      fieldValues: Array.from(this.fieldValues.values())
    }
  }

}
