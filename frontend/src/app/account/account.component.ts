import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { IUser } from '../Models/iuser';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',
})
export class AccountComponent {
  user: IUser | null | undefined;
  isChangePhotoModalOpen: boolean = false;
  isUpdateDataModalOpen: boolean = false;
  updatedUsername: string = '';
  updatedEmail: string = '';
  userImg: string = '';
  selectedFile: File | null = null;

  constructor(private authSvc: AuthService) {}

  ngOnInit() {
    const userJson = localStorage.getItem('accessData');
    if (userJson) {
      this.authSvc.authSubject.subscribe({
        next: (user) => {
          this.user = user;
          this.updatedUsername = user?.username || '';
          this.updatedEmail = user?.email || '';
          this.authSvc.getUserData(user!.id).subscribe((data) => {
            console.log(data?.avatar);
            this.userImg = data?.avatar || 'https://via.placeholder.com/150';
          });
        },
        error: (err) => {
          console.error('Failed to load user data', err);
        },
      });
    } else {
      console.error('User not logged in');
    }
  }

  openChangePhotoModal(): void {
    this.isChangePhotoModalOpen = true;
  }

  closeChangePhotoModal(): void {
    this.isChangePhotoModalOpen = false;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmitChangePhoto(): void {
    if (this.selectedFile && this.user) {
      this.authSvc.uploadAvatar(this.user.id, this.selectedFile).subscribe(
        (response) => {
          // Reload user data
          this.authSvc.getUserData(this.user!.id).subscribe((user) => {
            this.user = user;
            this.userImg = user.avatar || '';
            this.closeChangePhotoModal();
          });
        },
        (error) => {
          console.error('Failed to upload avatar:', error);
          this.closeChangePhotoModal();
        }
      );
    }
  }

  openUpdateDataModal(): void {
    this.isUpdateDataModalOpen = true;
  }

  closeUpdateDataModal(): void {
    this.isUpdateDataModalOpen = false;
  }

  onSubmitUpdateData(): void {
    if (this.user) {
      const updatedUser: Partial<IUser> = {
        username: this.user.username,
        email: this.updatedEmail,
        avatar: this.user.avatar,
        password: 'password',
      };

      this.authSvc.updateUserData(this.user.id, updatedUser).subscribe({
        next: (user) => {
          this.user = user;
          this.closeUpdateDataModal();
        },
        error: (err) => {
          console.error('Failed to update user data', err);
        },
      });
    }
  }
}
