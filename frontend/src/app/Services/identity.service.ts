import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { CreateIdentityDto } from '../Models/identityModel/create-identity-dto';
import { IdentityDto } from '../Models/identityModel/identity-dto';
import { Observable, catchError, throwError } from 'rxjs';
import { UpdateIdentityDto } from '../Models/identityModel/update-identity-dto';

@Injectable({
  providedIn: 'root',
})
export class IdentityService {
  private identityUrl = environment.identitiesUrl;
  constructor(private http: HttpClient, private authSvc: AuthService) {}

  // Method to create a new identity
  createIdentity(
    userId: number,
    identityData: CreateIdentityDto
  ): Observable<IdentityDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
      'Content-Type': 'application/json',
    });
    const identityId = this.authSvc.authSubject.subscribe((data) => {
      return data?.id;
    });
    return this.http
      .post<IdentityDto>(`${this.identityUrl}/${userId}`, identityData, {
        headers,
      })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to get an identity by its ID
  getIdentity(id: number): Observable<IdentityDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http
      .get<IdentityDto>(`${this.identityUrl}/${id}`, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to update an existing identity
  updateIdentity(
    id: number,
    identityData: UpdateIdentityDto
  ): Observable<IdentityDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
      'Content-Type': 'application/json',
    });
    return this.http
      .put<IdentityDto>(`${this.identityUrl}/${id}`, identityData, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to delete an identity by its ID
  deleteIdentity(id: number): Observable<void> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http
      .delete<void>(`${this.identityUrl}/${id}`, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to get all identities associated with the authenticated user
  getAllIdentitiesByUser(): Observable<IdentityDto[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http
      .get<IdentityDto[]>(`${this.identityUrl}/user`, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Error handling method
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      switch (error.status) {
        case 400:
          errorMessage = 'Bad request';
          break;
        case 401:
          errorMessage = 'Unauthorized';
          break;
        case 403:
          errorMessage = 'Forbidden';
          break;
        case 404:
          errorMessage = 'Not found';
          break;
        case 500:
          errorMessage = 'Internal server error';
          break;
        default:
          if (error.error && error.error.message)
            errorMessage = error.error.message;
          break;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
