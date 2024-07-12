import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GuestGuard {
  constructor(private authSvc: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    const isLoggedIn = this.authSvc.syncIsLoggedIn;
    console.log('GuestGuard canActivate:', isLoggedIn);
    if (this.authSvc.syncIsLoggedIn) {
      console.log('User already logged in, redirecting to /dashboard');
      this.router.navigate(['/dashboard']);
    }
    return !this.authSvc.syncIsLoggedIn;
  }

  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    return this.canActivate(childRoute, state);
  }
}
