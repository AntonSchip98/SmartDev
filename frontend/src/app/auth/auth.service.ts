import { Injectable } from '@angular/core';
import { IUser } from '../Models/iuser';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  tap,
  throwError,
} from 'rxjs';
import { environment } from '../../environments/environment.development';
import { IRegisterData } from '../Models/iregister-data';
import { ILoginData } from '../Models/ilogin-data';

type AccessData = {
  token: string;
  user: IUser;
};

type IError = {
  message: string;
  field?: string;
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  userUrl: string = environment.usersUrl;
  //semplifica il lavoro con i JSON Web Tokens (JWT),offre metodi utili per decodificare i token,
  //verificare la loro scadenza, e ottenere altre informazioni dal token,permette di chiamare metodi
  // come isTokenExpired e getTokenExpirationDate.
  jwtHelper: JwtHelperService = new JwtHelperService();

  // Crea un BehaviorSubject che inizialmente contiene null, indicando che l'utente non è loggato.
  authSubject = new BehaviorSubject<IUser | null>(null);

  //contiene i dati dell'utente loggato oppure null,Converte il BehaviorSubject in un Observable, che
  // può essere sottoscritto dai componenti per ricevere aggiornamenti sullo stato dell'utente.
  user$ = this.authSubject.asObservable();

  // È una proprietà che contiene lo stato attuale di login come un booleano sincrono. Viene aggiornata dal tap nel isLoggedIn$.
  syncIsLoggedIn: boolean = false;

  // È un Observable che emette true se un utente è loggato e false altrimenti.
  isLoggedIn$ = this.user$.pipe(
    map((user) => !!user),
    tap((user) => (this.syncIsLoggedIn = user)) //tap permette di eseguire effetti collaterali per ogni valore emesso senza modificarlo.
    //In questo caso, aggiorna la variabile syncIsLoggedIn con il valore dell'utente corrente.
  );

  constructor(private http: HttpClient, private router: Router) {
    this.restoreUser();
  }

  register(newUser: IRegisterData): Observable<IUser> {
    return this.http.post<IUser>(`${this.userUrl}/register`, newUser).pipe(
      tap((user) => {
        // Handle the response if necessary
      }),
      catchError(this.handleError)
    );
  }

  //Se il login ha successo, aggiorna il BehaviorSubject, salva i dati di accesso nel localStorage e imposta
  // un timer per il logout automatico.
  login(loginData: ILoginData): Observable<IUser> {
    return this.http.post<IUser>(`${this.userUrl}/login`, loginData).pipe(
      tap((data) => {
        console.log('Login successful, received data:', data); // Log per vedere i dati ricevuti
        if (data && data.token) {
          this.authSubject.next(data);
          console.log('AuthSubject updated:', this.authSubject.value); // Log per vedere l'aggiornamento del subject
          console.log(JSON.stringify(data));
          localStorage.setItem('accessData', JSON.stringify(data));
          this.autoLogout(data.token);
        } else {
          console.error('Login response does not contain token');
        }
      }),
      catchError(this.handleError)
    );
  }

  //Imposta il BehaviorSubject a null, rimuove i dati di accesso dal localStorage e reindirizza l'utente alla
  // pagina di login.
  logout(): void {
    this.authSubject.next(null);
    localStorage.removeItem('accessData');
    this.router.navigate(['/auth/login']);
  }

  // Recupera e verifica il token di accesso dal localStorage. Se il token è scaduto, restituisce
  // una stringa vuota.
  getAccessToken(): string {
    const userJson = localStorage.getItem('accessData');
    if (!userJson) return '';

    const accessData: AccessData = JSON.parse(userJson);
    if (this.jwtHelper.isTokenExpired(accessData.token)) return '';

    return accessData.token;
  }

  // Imposta un timer per eseguire il logout automatico quando il token JWT scade.
  autoLogout(token: string): void {
    const expDate = this.jwtHelper.getTokenExpirationDate(token) as Date;
    const expMs = expDate.getTime() - new Date().getTime();

    setTimeout(() => {
      this.logout();
    }, expMs);
  }

  //Ripristina i dati dell'utente dal localStorage e imposta il timer di auto-logout se il token è valido.
  restoreUser(): void {
    const userJson = localStorage.getItem('accessData');
    if (!userJson) return;

    const accessData: AccessData = JSON.parse(userJson);
    if (this.jwtHelper.isTokenExpired(accessData.token)) return;

    this.authSubject.next(accessData.user);
    this.autoLogout(accessData.token);
  }

  // Method to get user data by ID
  getUserData(id: number): Observable<IUser> {
    return this.http
      .get<IUser>(`${this.userUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // Method to update user data
  updateUserData(id: number, updatedUser: Partial<IUser>): Observable<IUser> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.getAccessToken()}`,
    });
    return this.http
      .put<IUser>(`${this.userUrl}/${id}`, updatedUser, { headers })
      .pipe(
        tap((response) => {
          const updatedAccessData: AccessData = {
            token: response.token!,
            user: response,
          };
          console.log(updatedAccessData);
          this.authSubject.next(response); // Update the current user data in the BehaviorSubject
          localStorage.setItem('accessData', JSON.stringify(updatedAccessData)); // Update localStorage
        }),
        catchError(this.handleError)
      );
  }

  // Method to upload user avatar
  uploadAvatar(userId: number, avatar: File): Observable<string> {
    const formData: FormData = new FormData();
    formData.append('avatar', avatar);

    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.getAccessToken()}`,
    });

    return this.http
      .post(`${this.userUrl}/${userId}/avatar`, formData, {
        headers,
        responseType: 'text',
      })
      .pipe(
        map((response) => {
          try {
            this.getUserData(userId).subscribe((user) => {
              this.authSubject.next(user);
            });
            const parsedResponse = JSON.parse(response);
            return parsedResponse;
          } catch (e) {
            return { message: response };
          }
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Error uploading avatar:', error);
          return throwError(() => error);
        })
      );
  }
  //gestione errori
  /*  errors(err: any): Error {
    switch (err.error) {
      case 'Email and Password are required':
        return new Error('Email e password obbligatorie');
      case 'Email already exists':
        return new Error('Utente esistente');
      case 'Email format is invalid':
        return new Error('Email scritta male');
      case 'Cannot find user':
        return new Error('Utente inesistente');
      default:
        return new Error('Errore');
    }
  } */

  handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage: IError = { message: 'An unknown error occurred' };

    if (error.error instanceof ErrorEvent) {
      // Client-side or network error
      errorMessage = { message: error.error.message };
    } else {
      // Backend error
      switch (error.status) {
        case 400:
          errorMessage = { message: 'Bad request' };
          break;
        case 401:
          errorMessage = { message: 'Unauthorized' };
          break;
        case 403:
          errorMessage = { message: 'Forbidden' };
          break;
        case 404:
          errorMessage = { message: 'Not found' };
          break;
        case 500:
          errorMessage = { message: 'Internal server error' };
          break;
        default:
          if (error.error && error.error.message) {
            errorMessage = { message: error.error.message };
          }
          break;
      }
    }

    return throwError(() => errorMessage);
  }
}
