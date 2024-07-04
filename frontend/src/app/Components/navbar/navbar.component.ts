import {
  Component,
  EventEmitter,
  HostListener,
  Output,
  ViewChild,
} from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { IUser } from '../../Models/iuser';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IdentityService } from '../../Services/identity.service';
import { CreateIdentityDto } from '../../Models/identityModel/create-identity-dto';
import { UserService } from '../../Services/comunication.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  isUserLoggedIn: boolean = false;
  isNavbarOpen: boolean = false;
  user: IUser | null | undefined;
  userImg: string = '';
  isAddIdentityModalOpen: boolean = false;
  addIdentityForm: FormGroup;

  @Output() navbarToggled = new EventEmitter<boolean>();

  constructor(
    private authSvc: AuthService,
    private fb: FormBuilder,
    private identityService: IdentityService,
    private communicationService: UserService
  ) {
    this.addIdentityForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.authSvc.isLoggedIn$.subscribe((data) => {
      console.log("L'utente è loggato? " + data.valueOf());
      this.isUserLoggedIn = data;
      if (!data) {
        this.isNavbarOpen = false;
        this.navbarToggled.emit(this.isNavbarOpen);
      } else {
        this.isNavbarOpen = true;
        this.navbarToggled.emit(this.isNavbarOpen);
      }
    });

    this.authSvc.user$.subscribe((data) => {
      this.user = data;
      if (data) {
        this.authSvc.getUserData(this.user!.id).subscribe((data) => {
          console.log(data?.avatar);
          this.userImg = data?.avatar || 'https://via.placeholder.com/40';
        });
      }
    });
  }

  toggleNavbar() {
    this.isNavbarOpen = !this.isNavbarOpen;
    this.navbarToggled.emit(this.isNavbarOpen);
  }

  logout() {
    this.authSvc.logout();
    this.isNavbarOpen = false;
    this.navbarToggled.emit(this.isNavbarOpen);
  }

  openAddIdentityModal() {
    this.isAddIdentityModalOpen = true;
    this.closeNavbarOnMobile();
  }

  closeAddIdentityModal() {
    this.isAddIdentityModalOpen = false;
    this.addIdentityForm.reset();
  }

  onSubmit() {
    if (this.addIdentityForm.valid) {
      const identityData: CreateIdentityDto = {
        title: this.addIdentityForm.get('title')?.value,
        description: this.addIdentityForm.get('description')?.value,
      };

      this.identityService
        .createIdentity(this.user?.id!, identityData)
        .subscribe(() => {
          this.closeAddIdentityModal();
          this.communicationService.announceIdentityAdded(); // Announce the identity added event
        });
    }
  }

  preventSidebarClose(event: Event) {
    event.stopPropagation();
  }

  closeNavbarOnMobile() {
    if (window.innerWidth < 768) {
      this.isNavbarOpen = false;
    }
  }
}
