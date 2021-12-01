import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { User } from "./user.model";
import { TokenStorageService } from "./auth/token-storage.service";

const baseUrl = 'http://localhost:49154/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + this.tokenStorage.getToken()
  })

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService) {}

  get(id: number): Observable<User> {
    return this.http.get<User>(`${baseUrl}/${id}`, {headers: this.headers});
  }

}
