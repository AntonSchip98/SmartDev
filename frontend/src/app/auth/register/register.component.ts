import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { IUser } from '../../Models/iuser';
import { IRegisterData } from '../../Models/iregister-data';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerData: IRegisterData = {
    username: 'schipani99',
    email: 'schipani99@gmail.com',
    password: 'password',
  };

  constructor(private authSvc: AuthService, private router: Router) {}

  signUp() {
    this.authSvc.register(this.registerData).subscribe((data) => {
      this.router.navigate(['/dashboard']);
    });
  }
}
