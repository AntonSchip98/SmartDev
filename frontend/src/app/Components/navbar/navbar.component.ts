import { Component, HostListener } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { IIdentity } from '../../Models/i-identity';
import { IdentitiesService } from '../../Services/identities.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  isUserLoggedIn: boolean = false;
  isUserRegistered: boolean = false;
  showDropdown = false;
  isMobileMenuOpen = false;
  isNavbarOpen = false;
  isAddIdentityModalOpen = false;
  addIdentityForm: FormGroup;

  constructor(
    private authSvc: AuthService,
    private identitiesService: IdentitiesService,
    private fb: FormBuilder
  ) {
    this.addIdentityForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.authSvc.isLoggedIn$.subscribe((data) => {
      this.isUserLoggedIn = data;
    });
  }

  logout() {
    this.authSvc.logout();
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  toggleNavbar(event: Event) {
    event.stopPropagation(); // Prevenire la propagazione del click
    this.isNavbarOpen = !this.isNavbarOpen;
  }

  openAddIdentityModal() {
    this.isAddIdentityModalOpen = true;
    this.closeNavbarOnMobile();
  }

  closeAddIdentityModal() {
    this.isAddIdentityModalOpen = false;
  }

  onSubmit() {
    if (this.addIdentityForm.valid) {
      const identityData: Partial<IIdentity> = {
        title: this.addIdentityForm.get('title')?.value,
        description: this.addIdentityForm.get('description')?.value,
      };

      const userId = this.authSvc.currentUser?.id;
      if (userId) {
        this.identitiesService
          .createIdentity(userId, identityData)
          .subscribe(() => {
            this.closeAddIdentityModal();
            this.closeNavbarOnMobile();
          });
      }
    }
  }

  @HostListener('window:click', ['$event'])
  onScreenClick(event: Event) {
    if (this.isNavbarOpen && window.innerWidth < 768) {
      this.isNavbarOpen = false;
    }
  }

  preventSidebarClose(event: Event) {
    event.stopPropagation();
  }

  preventModalClose(event: Event) {
    event.stopPropagation();
  }

  closeNavbarOnMobile() {
    if (window.innerWidth < 768) {
      this.isNavbarOpen = false;
    }
  }
}
