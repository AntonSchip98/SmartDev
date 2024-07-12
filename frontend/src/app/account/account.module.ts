import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AccountRoutingModule } from './account-routing.module';
import { AccountComponent } from './account.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [AccountComponent],
  imports: [CommonModule, AccountRoutingModule, FormsModule],
})
export class AccountModule {}
