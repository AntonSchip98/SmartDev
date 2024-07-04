import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { ILoginData } from '../../Models/ilogin-data';

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

  constructor(private authSvc: AuthService, private router: Router) {}

  signIn() {
    this.authSvc.login(this.loginData).subscribe({
      next: (data) => {
        console.log('Navigating to dashboard with data:', data); // Aggiungi questo log
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Login failed', err);
        // Gestisci l'errore di login, ad esempio mostrando un messaggio all'utente
      },
    });
  }
}
