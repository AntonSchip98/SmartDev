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
import { Router } from '@angular/router';
import { IdentityDto } from '../../Models/identityModel/identity-dto';

type HelpFields = 'title' | 'description';

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
  identities: IdentityDto[] = [];

  showHelp: Record<HelpFields, boolean> = {
    title: false,
    description: false,
  };

  @Output() navbarToggled = new EventEmitter<boolean>();

  constructor(
    private authSvc: AuthService,
    private fb: FormBuilder,
    private identityService: IdentityService,
    private communicationService: UserService,
    private router: Router
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

        this.loadIdentities();
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

    // Subscribe to the event to reload identities when an identity is added or deleted
    this.communicationService.identityAdded$.subscribe(() => {
      this.loadIdentities();
    });
  }

  loadIdentities() {
    this.identityService.getAllIdentitiesByUser().subscribe((identities) => {
      this.identities = identities;
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

  toggleHelp(field: HelpFields): void {
    const isCurrentlyOpen = this.showHelp[field];
    Object.keys(this.showHelp).forEach((key) => {
      this.showHelp[key as HelpFields] = false;
    });
    this.showHelp[field] = !isCurrentlyOpen;
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
          this.loadIdentities();
        });
    }
  }

  navigateToIdentity(identityId: number): void {
    this.router.navigate(['/identity', identityId]);
    this.closeNavbarOnMobile();
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
