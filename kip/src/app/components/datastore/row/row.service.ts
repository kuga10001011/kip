import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { Row } from "./row.model";
import { TokenStorageService } from "../../user/auth/token-storage.service";
import { FieldValue } from "../field-value.model";
import { Datastore } from "../datastore.model";
import { Value } from "../value/value.model";

const baseUrl = "http://localhost:49154/row"

@Injectable({
  providedIn: 'root'
})
export class RowService {

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + this.tokenStorage.getToken()
  })

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService) { }

  getAll(): Observable<Row[]> {
    return this.http.get<Row[]>(`${baseUrl}`);
  }

  getById(id: number): Observable<Row> {
    return this.http.get<Row>(`${baseUrl}/${id}`, {headers: this.headers});
  }

  delete(data: Row): Observable<any> {
    return this.http.delete(`${baseUrl}/delete/${data.id}`, {headers: this.headers});
  }

  add(fieldValues: Set<FieldValue>, datastore: Datastore): Observable<Row> {
    return this.http.post<Row>(`${baseUrl}/add`, {fieldValues: Array.from(fieldValues), datastore: datastore}, {headers: this.headers});
  }

  update(rowId: number, valuesToAdd: Set<FieldValue>, valuesToUpdate: Map<number, Value>, valuesToDelete: Set<number>): Observable<Row> {
    let valuesToUpdateObject = Array.from(valuesToUpdate).reduce((valuesToUpdateObject, [key, value]) => (
      Object.assign(valuesToUpdateObject, { [key]: value })
    ), {});
    return this.http.put<Row>(`${baseUrl}/update/${rowId}`, {valuesToAdd: Array.from(valuesToAdd), valuesToUpdate: valuesToUpdateObject, valuesToDelete: Array.from(valuesToDelete)}, {headers: this.headers});
  }

}
