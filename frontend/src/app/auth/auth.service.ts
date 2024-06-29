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

  authSubject = new BehaviorSubject<IUser | null>(null); //se nel behavioursubject c'è null significa che l'utente non è loggato, altrimenti conterrà l'oggetto user con tutte le sue info

  user$ = this.authSubject.asObservable(); //contiene i dati dell'utente loggato oppure null

  syncIsLoggedIn: boolean = false;
  isLoggedIn$ = this.user$.pipe(
    map((user) => !!user),
    tap((user) => (this.syncIsLoggedIn = user))
  ); //restituisce true se lò'utente è loggato, false se non lo è
  //!!user è come scrivere Boolean(user)
  //isLoggedIn$ = this.user$.pipe(map(user => Boolean(user)))

  constructor(
    private http: HttpClient, //per le chiamate http
    private router: Router //per i redirect
  ) {
    this.restoreUser(); //come prima cosa controllo se è già attiva una sessione, e la ripristino
  }

  register(newUser: Partial<IUser>): Observable<IUser> {
    return this.http.post<IUser>(`${this.userUrl}/register`, newUser);
  }

  login(loginData: ILoginData): Observable<ILoginData> {
    return this.http.post<AccessData>(`${this.userUrl}/login`, loginData).pipe(
      tap((data: any) => {
        const accessData = {
          accessToken: data.token,
          user: data,
        };
        this.authSubject.next(accessData.user); //comunico al subject che l'utente si è loggato
        localStorage.setItem('accessData', JSON.stringify(accessData));
        /*         console.log(accessData);
         */ this.autoLogout(accessData.accessToken);
      })
    );
  }

  logout() {
    this.authSubject.next(null); //comunico al subject che l'utente si è sloggato
    localStorage.removeItem('accessData'); //cancello i dati dell'utente
    /*     this.syncIsLoggedIn = false; //?????
     */ this.router.navigate(['/auth/login']); //mando via l'utente loggato
  }

  getAccessToken(): string {
    const userJson = localStorage.getItem('accessData'); //recupero io dati di accesso
    if (!userJson) return ''; //se l'utente non si è mai loggato blocca tutto

    const accessData: AccessData = JSON.parse(userJson); //se viene eseguita questa riga significa che i dati ci sono, quindi la converto da json ad oggetto per permetterne la manipolazione
    if (this.jwtHelper.isTokenExpired(accessData.accessToken)) return ''; //ora controllo se il token è scaduto, se lo è fermiamo la funzione

    return accessData.accessToken;
  }

  autoLogout(jwt: string) {
    const expDate = this.jwtHelper.getTokenExpirationDate(jwt) as Date; //trovo la data di scadenza del token

    if (expDate) {
      const expMs = expDate.getTime() - new Date().getTime(); //sottraggo i ms della data/ora di oggi da quella nel jwt

      //avvio un timer, quando sarà passato il numero di ms necessari per la scadenza del token, avverrà il logout
      setTimeout(() => {
        this.logout();
      }, expMs);
    } else {
      console.error('Invalid JWT: Cannot determine expiration date');
      this.logout();
    }
  }

  restoreUser() {
    const userJson = localStorage.getItem('accessData'); //recupero io dati di accesso
    if (!userJson) return; //se l'utente non si è mai loggato blocca tutto

    const accessData: AccessData = JSON.parse(userJson); //se viene eseguita questa riga significa che i dati ci sono, quindi la converto da json ad oggetto per permetterne la manipolazione
    if (this.jwtHelper.isTokenExpired(accessData.accessToken)) return; //ora controllo se il token è scaduto, se lo è fermiamo la funzione

    //se nessun return viene eseguito proseguo
    this.authSubject.next(accessData.user); //invio i dati dell'utente al behaviorsubject
    this.autoLogout(accessData.accessToken); //riavvio il timer per la scadenza della sessione
    /*     this.syncIsLoggedIn = true; // Ensure syncIsLoggedIn is set to true ????
     */
  }

  getUserId(): Observable<number | undefined> {
    return this.user$.pipe(map((user) => (user ? user.id : undefined)));
  }

  get currentUser(): IUser | null {
    return this.authSubject.value;
  }

  errors(err: any) {
    switch (err.error) {
      case 'Email and Password are required':
        return new Error('Email e password obbligatorie');
        break;
      case 'Email already exists':
        return new Error('Utente esistente');
        break;
      case 'Email format is invalid':
        return new Error('Email scritta male');
        break;
      case 'Cannot find user':
        return new Error('utente inesistente');
        break;
      default:
        return new Error('Errore');
        break;
    }
  }
}
