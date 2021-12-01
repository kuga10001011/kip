import { Deserializable } from "../../../shared/deserializable";

export class Field implements Deserializable {

  id?: number;
  name!: string;
  required!: boolean;
  type!: string;
  allowedValues: Set<string> = new Set<string>();
  summary!: string;

  constructor() {
    this.setSummary();
  }

  deserialize(input: any): this {
    Object.assign(this, input);
    this.allowedValues = new Set(input.allowedValues);
    if (!input.summary) {
      this.setSummary();
    }
    else {
      this.summary = input.summary;
    }
    return this;
  }

  setSummary() : void {
    const summaryData: any[] = [];
    summaryData.push(this.name);
    summaryData.push(this.type);
    summaryData.push(this.required);
    if (this.allowedValues.size > 0) {
      summaryData.push(JSON.stringify(Array.from(this.allowedValues)));
    }
    this.summary = summaryData.join(" | ");
  }

  set(name: string, type: string, required: boolean): this {
    this.name = name;
    this.type = type;
    this.required = required;
    return this;
  }

  setAllowedValues(allowedValues: Set<string>): void {
    this.allowedValues = allowedValues;
  }

  toJSON() {
    return {
      id: this.id,
      name: this.name,
      type: this.type,
      required: this.required,
      allowedValues: Array.from(this.allowedValues),
      summary: this.summary
    }
  }

}
