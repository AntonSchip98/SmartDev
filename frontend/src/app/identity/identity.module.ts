import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { IdentityRoutingModule } from './identity-routing.module';
import { IdentityComponent } from './identity.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from '../auth.interceptor';

@NgModule({
  declarations: [IdentityComponent],
  imports: [
    CommonModule,
    IdentityRoutingModule,
    ReactiveFormsModule,
    RouterModule.forChild([{ path: ':id', component: IdentityComponent }]),
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
})
export class IdentityModule {}
