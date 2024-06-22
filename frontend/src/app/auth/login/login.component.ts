import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { ILoginData } from '../../Models/i-login-data';
import { IUser } from '../../Models/i-user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginData: ILoginData = {
    username: 'schipani99',
    password: 'password',
  };
  errorMessage: string | null = null;
  constructor(private authSvc: AuthService, private router: Router) {}

  signIn() {
    this.authSvc.login(this.loginData).subscribe({
      next: (data) => {
        this.router.navigate(['dashboard']);
      },
      error: (err) => {
        this.errorMessage =
          'Login failed: ' + (err.error.message || err.message);
      },
    });
  }
}
