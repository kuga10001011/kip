import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { UserService } from "../user.service";
import { User } from "../user.model";
import { AuthService } from "../auth/auth.service";
import { TokenStorageService } from "../auth/token-storage.service";

@Component({
  selector: 'app-user-manage',
  templateUrl: './user-manage.component.html',
  styleUrls: ['./user-manage.component.css']
})
export class UserManageComponent implements OnInit {

  userForm: FormGroup;
  mode: 'add' | 'edit' | 'login' = 'add';
  hidePassword: boolean = true;

  constructor(private userService: UserService, private authService: AuthService, private tokenStorage: TokenStorageService, private activatedRoute: ActivatedRoute, private router: Router) {
    this.userForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.email])
    });
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.mode = params['mode'];
      if (this.mode == 'edit') {
        if (params['id']) {
          localStorage.setItem('user_manage_id', params['id']);
        }
        if (this.tokenStorage.getToken()) {
          this.prepopulateUserForm();
        }
      }
    });
  }

  prepopulateUserForm(): void {
    let user = undefined;
    const query_id = localStorage.getItem('user_manage_id');
    if (query_id == this.tokenStorage.getUser().id) {
      user = this.tokenStorage.getUser();
    }
    if (this.tokenStorage.getUser().roles.includes('ROLE_ADMIN') && query_id) {
      this.userService.get(Number(query_id)).subscribe(data => {
        user = new User().deserialize(data);
      });
    }
    if (user) {
      this.userForm.patchValue({username: user.username});
      this.userForm.patchValue({password: user.password});
      this.userForm.patchValue({email: user.email});
    }
  }

  submit(): void {
    let username = this.userForm.get('username')?.value;
    let password = this.userForm.get('password')?.value;
    let email = this.userForm.get('email')?.value;

    switch (this.mode) {
      case 'add':
        this.register(username, password, email);
        break;
      case 'edit':
        this.update(username, password, email);
        break;
      case 'login':
        this.login(username, password);
        break;
    }
  }

  update(username: string, password: string, email: string) {

  }

  register(username: string, password: string, email: string) {
    this.authService.register(username, email, password).subscribe(
      data => {
        console.log('user successfully added');
        console.log(data);
      },
      error => {
        console.log(error);
      },
      () => {
        this.login(username, password);
      }
    );
  }

  login(username: string, password: string): void {
    this.authService.login(username, password);
  }

}
