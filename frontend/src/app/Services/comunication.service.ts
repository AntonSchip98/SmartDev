import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'https://api.quotable.io/random';

  private identityAddedSource = new Subject<void>();

  identityAdded$ = this.identityAddedSource.asObservable();

  constructor(private http: HttpClient) {}

  getQuote(): Observable<{ content: string; author: string }> {
    return this.http.get<{ content: string; author: string }>(this.apiUrl);
  }

  announceIdentityAdded() {
    this.identityAddedSource.next();
  }
}
