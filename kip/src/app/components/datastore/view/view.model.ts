import { Deserializable } from "../../../shared/deserializable";
import { Field } from "../field/field.model";
import { User } from "../../user/user.model";

export class View implements Deserializable {

  id?: number;
  name!: string;
  fields: Array<Field> = new Array<Field>();
  users: Set<User> = new Set<User>();
  userIds: Set<number> = new Set<number>();

  deserialize(input: any): this {
    Object.assign(this, input);
    if (input.fields) {
      this.fields = Array.from(input.fields.map((field: any) => new Field().deserialize(field)));
    }
    if (input.users) {
      input.users.forEach((userData: any) => {
        const user = new User().deserialize(userData);
        this.users.add(user);
        this.userIds.add(user.id!);
      });
      this.users = new Set<User>(input.users.map((user: any) => {
        new User().deserialize(user)
      }));
    }
    return this;
  }

  toJSON() {
    return {
      id: this.id,
      name: this.name,
      fields: this.fields,
      users: Array.from(this.users)
    }
  }

}
