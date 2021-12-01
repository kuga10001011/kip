import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from "./components/user/auth/token-storage.service";
import { Router } from "@angular/router";
import { AuthService } from "./components/user/auth/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  title: string = 'kip';
  loggedIn: boolean = false;
  roles: string[] = [];
  userManagementParams: any = {mode: 'login'};
  showAdminContent: boolean = false;

  constructor(private tokenStorage: TokenStorageService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.loggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
      this.userManagementParams = {mode: 'edit', id: this.tokenStorage.getUser().id}
      if (this.roles) {
        this.showAdminContent = this.roles.includes('ROLE_ADMIN');
      }
    }
  }

  logout(): void {
    this.authService.logout();
  }

}
