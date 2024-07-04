import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
//L'obiettivo dell'interceptor è aggiungere il token JWT alle intestazioni delle richieste HTTP in
// modo che l'utente autenticato possa accedere alle risorse protette.
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authSvc: AuthService) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const accessToken = this.authSvc.getAccessToken();

    if (accessToken) {
      const newRequest = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + accessToken),
      });
      return next.handle(newRequest);
    } else {
      return next.handle(request);
    }
  }
}
