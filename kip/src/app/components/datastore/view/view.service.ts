import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { TokenStorageService } from "../../user/auth/token-storage.service";
import { Field } from "../field/field.model";
import { Observable } from "rxjs";
import { View } from "./view.model";

const baseUrl = 'http://localhost:49154/view';

@Injectable({
  providedIn: 'root'
})
export class ViewService {

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + this.tokenStorage.getToken()
  })

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService) {
  }

  add(datastoreId: number, name: string, fields: Array<Field>): Observable<View> {
    const params = {
      datastoreId: datastoreId,
      name: name
    }
    return this.http.post<View>(`${baseUrl}/add`, {fields: fields}, {headers: this.headers, params: params});
  }

  getAllForDatastore(datastoreId: number): Observable<View[]> {
    const params = {
      datastoreId: datastoreId,
      userId: this.tokenStorage.getUser().id
    }
    return this.http.get<View[]>(`${baseUrl}/`, {headers: this.headers, params: params});
  }

}
