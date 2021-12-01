import { Injectable } from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {TokenStorageService} from "../../user/auth/token-storage.service";
import {Observable} from "rxjs";

const baseUrl = 'http://localhost:49154/field';

@Injectable({
  providedIn: 'root'
})
export class FieldService {

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + this.tokenStorage.getToken()
  })

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService) {}

  getTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${baseUrl}/types`, {headers: this.headers});
  }

}
