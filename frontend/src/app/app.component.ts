import { Component } from '@angular/core';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';

  isNavbarOpen: boolean = true;

  constructor(private authService: AuthService) {
    this.authService.isLoggedIn$.subscribe((isLoggedIn) => {
      console.log("L'utente è loggato? " + isLoggedIn);
      this.isNavbarOpen = isLoggedIn;
    });
  }

  onNavbarToggled(isOpen: boolean) {
    this.isNavbarOpen = isOpen;
  }
}
