import { Component } from '@angular/core';
import { IdentityDto } from '../Models/identityModel/identity-dto';
import { TaskDto } from '../Models/taskModel/task-dto';
import { ActivatedRoute, Router } from '@angular/router';
import { IdentityService } from '../Services/identity.service';
import { TaskService } from '../Services/task.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../Services/comunication.service';

@Component({
  selector: 'app-identity',
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.scss',
})
export class IdentityComponent {
  identity: IdentityDto | null = null;
  tasks: TaskDto[] = [];
  menuTaskId: number | null = null;
  isEditTaskModalOpen = false;
  isEditIdentityModalOpen = false;
  editTaskForm: FormGroup;
  editIdentityForm: FormGroup;
  currentTaskId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private identityService: IdentityService,
    private taskService: TaskService,
    private fb: FormBuilder,
    private communicationService: UserService
  ) {
    this.editTaskForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      cue: ['', Validators.required],
      craving: ['', Validators.required],
      response: ['', Validators.required],
      reward: ['', Validators.required],
    });

    this.editIdentityForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      this.loadIdentity(id);
    });
  }

  loadIdentity(id: number): void {
    this.identityService.getIdentity(id).subscribe((identity) => {
      this.identity = identity;
      this.editIdentityForm.patchValue(identity);
      this.loadTasks(id);
    });
  }

  loadTasks(identityId: number): void {
    this.taskService.getAllTasksByIdentity(identityId).subscribe((tasks) => {
      this.tasks = tasks;
    });
  }

  toggleMenu(taskId: number) {
    if (this.menuTaskId === taskId) {
      this.menuTaskId = null;
    } else {
      this.menuTaskId = taskId;
    }
  }

  openEditTaskModal(task: TaskDto) {
    this.isEditTaskModalOpen = true;
    this.currentTaskId = task.id;
    this.editTaskForm.patchValue(task);
    this.menuTaskId = null; // Hide the menu after action
  }

  closeEditTaskModal() {
    this.animateModalClose('isEditTaskModalOpen');
  }

  onSubmitEditTask() {
    if (this.editTaskForm.valid && this.currentTaskId !== null) {
      const updatedTask: TaskDto = {
        ...this.editTaskForm.value,
        id: this.currentTaskId,
        identityId: this.identity!.id,
        completed: false, // or maintain the original state if needed
        createdAt: new Date().toISOString(), // or maintain the original date if needed
      };

      this.taskService
        .updateTask(this.currentTaskId, updatedTask)
        .subscribe(() => {
          this.closeEditTaskModal();
          if (this.identity) {
            this.loadTasks(this.identity.id);
          }
        });
    }
  }

  openEditIdentityModal() {
    this.isEditIdentityModalOpen = true;
  }

  closeEditIdentityModal() {
    this.animateModalClose('isEditIdentityModalOpen');
  }

  onSubmitEditIdentity() {
    if (this.editIdentityForm.valid && this.identity) {
      const updatedIdentity: IdentityDto = {
        ...this.identity,
        ...this.editIdentityForm.value,
      };

      this.identityService
        .updateIdentity(updatedIdentity.id, updatedIdentity)
        .subscribe(() => {
          this.closeEditIdentityModal();
          this.loadIdentity(updatedIdentity.id);
        });
    }
  }

  deleteIdentity() {
    if (this.identity) {
      this.identityService.deleteIdentity(this.identity.id).subscribe(() => {
        this.communicationService.announceIdentityAdded(); // Announce the identity deleted event
        this.router.navigate(['/dashboard']);
      });
    }
  }

  deleteTask(taskId: number) {
    this.taskService.deleteTask(taskId).subscribe(() => {
      if (this.identity) {
        this.loadTasks(this.identity.id);
      }
      this.menuTaskId = null; // Hide the menu after action
    });
  }

  private animateModalClose(modalProperty: keyof IdentityComponent) {
    const modalElement = document.querySelector('.modal-content');
    if (modalElement) {
      modalElement.classList.remove('modal-enter');
      modalElement.classList.add('modal-leave');
      setTimeout(() => {
        (this[modalProperty] as boolean) = false;
      }, 300); // Match the duration of the animation
    } else {
      (this[modalProperty] as boolean) = false;
    }
  }
}
