import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { IUser } from '../../Models/i-user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerData: Partial<IUser> = {};
  errorMessage: string | null = null;

  constructor(private authSvc: AuthService, private router: Router) {}

  signUp() {
    this.authSvc.register(this.registerData).subscribe({
      next: (data) => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Registration failed. Please try again.';
      },
    });
  }
}
