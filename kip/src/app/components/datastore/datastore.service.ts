import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { TokenStorageService } from "../user/auth/token-storage.service";
import { Observable } from "rxjs";
import { Datastore } from "./datastore.model";
import { FieldService } from "./field/field.service";
import { Field } from "./field/field.model";

const baseUrl = 'http://localhost:49154/datastore';

@Injectable({
  providedIn: 'root'
})
export class DatastoreService {

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + this.tokenStorage.getToken()
  })

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService, private fieldService: FieldService) {}

  getAll() : Observable<Datastore[]> {
    if (this.tokenStorage.getUser() && this.tokenStorage.getUser().roles) {
      if (this.tokenStorage.getUser().roles.includes('ROLE_ADMIN')) {
        return this.http.get<Datastore[]>(`${baseUrl}/`, {headers: this.headers});
      }
      else {
        let params = {
          userId: this.tokenStorage.getUser().id
        }
        return this.http.get<Datastore[]>(`${baseUrl}/`, {headers: this.headers, params: params});
      }
    }
    throw Error('unauthorized --- must sign in to view datastores');
  }

  delete(data: Datastore): Observable<any> {
    return this.http.delete(`${baseUrl}/delete/${data.id}`, {headers: this.headers});
  }

  getTypes(): Observable<string[]> {
    return this.fieldService.getTypes();
  }

  add(name: string, fields: Field[], userId: number): Observable<Datastore> {
    return this.http.post<Datastore>(`${baseUrl}/add`, {name: name, fields: fields, userId: userId}, {headers: this.headers});
  }

  get(id: number): Observable<Datastore> {
    return this.http.get<Datastore>(`${baseUrl}/${id}`, {headers: this.headers});
  }

  update(datastoreId: number, userId: number, fieldsToAdd: Set<Field>, fieldsToDelete: Set<number>, fieldsToUpdate: Map<number, Field>, name: string): Observable<Datastore> {
    let params = {
      datastoreId: datastoreId
    }
    let fieldsToUpdateObject = Array.from(fieldsToUpdate).reduce((fieldsToUpdateObject, [key, value]) => (
      Object.assign(fieldsToUpdateObject, { [key]: value })
    ), {});
    return this.http.put<Datastore>(`${baseUrl}/update/`, {name: name, fieldsToAdd: Array.from(fieldsToAdd), fieldsToDelete: Array.from(fieldsToDelete), fieldsToUpdate: fieldsToUpdateObject, userId: userId}, {headers: this.headers, params: params});
  }

}
