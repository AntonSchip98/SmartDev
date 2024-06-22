import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { IUser } from '../Models/i-user';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.development';
import { ILoginData } from '../Models/i-login-data';
import { Router } from '@angular/router';

type AccessData = {
  accessToken: string;
  user: IUser;
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  userUrl: string = environment.usersUrl;

  jwtHelper: JwtHelperService = new JwtHelperService(); //ci permette di lavorare facilmente con i jwt
  authSubject = new BehaviorSubject<IUser | null>(null); //se nel behaviour subject c'è null l'utente non è loggato, altrimenti conterrà l'oggetto con tutte le sue info

  user$ = this.authSubject.asObservable(); // contiene i dati dell'utente loggato oppure null
  isLoggedIn$ = this.user$.pipe(map((user) => !!user)); //posso capire se l'utente è loggato(true) o no (false)

  constructor(private http: HttpClient, private router: Router) {
    this.restoreUser();
  }

  register(newUser: Partial<IUser>): Observable<AccessData> {
    return this.http.post<AccessData>(`${this.userUrl}/register`, newUser);
  }

  login(loginData: Partial<IUser>): Observable<AccessData> {
    return this.http.post<AccessData>(`${this.userUrl}/login`, loginData).pipe(
      tap((data) => {
        this.authSubject.next(data.user); //comunico al subject che l utente si è loggato
        localStorage.setItem('accessData', JSON.stringify(data));

        //attivare l'autologout
        this.autoLogout(data.accessToken);
      })
    );
  }

  logout() {
    this.authSubject.next(null); //comunico al subject che l utente si è sloggato
    localStorage.removeItem('accessData'); //cancello i dati dell'utente
    this.router.navigate(['/auth/login']); //mando via l'utente loggato
  }

  autoLogout(jwt: string) {
    const expDate = this.jwtHelper.getTokenExpirationDate(jwt);
    if (expDate) {
      const expMs = expDate.getTime() - new Date().getTime();
      setTimeout(() => {
        this.logout();
      }, expMs);
    } else {
      console.error('Invalid JWT: Cannot determine expiration date');
      // Fallback: Log out immediately if the token is invalid
      this.logout();
    }
  }

  restoreUser() {
    const userJson = localStorage.getItem('accessData'); //recupero i dati di accesso
    if (!userJson) return;

    const accessData: AccessData = JSON.parse(userJson);

    if (this.jwtHelper.isTokenExpired(accessData.accessToken)) return;

    this.authSubject.next(accessData.user);
    this.autoLogout(accessData.accessToken);
  }
}
