import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "../user.model";
import { TokenStorageService } from "./token-storage.service";
import { Router } from "@angular/router";

const baseUrl = 'http://localhost:49154/auth';
const headers = {
  'Content-Type': 'application/json'
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService, private router: Router) {}

  login(username: string, password: string) {
    let body = {
      username: username,
      password: password
    }
    this.http.post<any>(`${baseUrl}/login`, body, {headers: headers}).subscribe(
      data => {
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveUser(new User().deserialize(data.user));
      },
      error => {
        console.log(error);
      },
      () => {
        this.router.navigate(['/user/manage'], {queryParams: {mode: 'edit', id: this.tokenStorage.getUser().id}}).then(() => window.location.reload());
      }
    );
  }

  logout(): void {
    this.tokenStorage.signOut();
    localStorage.removeItem('user_manage_id');
    this.router.navigate(['/home']).then(() => window.location.reload());
  }

  register(username: string, email: string, password: string): Observable<User> {
    let body = {
      username: username,
      password: password,
      email: email
    }
    return this.http.post<User>(`${baseUrl}/signup`, body);
  }

}
