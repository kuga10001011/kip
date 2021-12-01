import { Deserializable } from "../../shared/deserializable";
import { Datastore } from "../datastore/datastore.model";

export class User implements Deserializable {

  id?: number;
  username!: string;
  password?: string;
  email?: string;
  datastores: Set<Datastore> = new Set<Datastore>();
  roles: Set<string> = new Set<string>();

  deserialize(input: any): this {
    Object.assign(this, input);
    if (input.datastores) {
      input.datastores.map((datastore: Datastore) => {this.datastores.add(new Datastore().deserialize(datastore))});
    }
    return this;
  }

  toJSON() {
    return {
      id: this.id,
      username: this.username,
      password: this.password,
      email: this.email,
      datastores: Array.from(this.datastores),
      roles: Array.from(this.roles)
    }
  }

}
