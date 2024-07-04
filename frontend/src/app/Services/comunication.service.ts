import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private identityAddedSource = new Subject<void>();

  identityAdded$ = this.identityAddedSource.asObservable();

  constructor() {}

  announceIdentityAdded() {
    this.identityAddedSource.next();
  }
}
