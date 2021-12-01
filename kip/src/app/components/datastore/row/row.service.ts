import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { Row } from "./row.model";
import { TokenStorageService } from "../../user/auth/token-storage.service";
import { FieldValue } from "../field-value.model";
import { Datastore } from "../datastore.model";

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
    let params = {
      'id': id
    }
    return this.http.get<Row>(`${baseUrl}`, {params: params});
  }

  delete(data: Row): Observable<any> {
    return this.http.delete(`${baseUrl}/delete/${data.id}`, {headers: this.headers});
  }

  add(fieldValues: Set<FieldValue>, datastore: Datastore): Observable<Row> {
    return this.http.post<Row>(`${baseUrl}/add`, {fieldValues: Array.from(fieldValues), datastore: datastore}, {headers: this.headers});
  }

}
