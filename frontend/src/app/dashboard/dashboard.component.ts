import { Component, OnInit } from '@angular/core';
import { IdentitiesService } from '../Services/identities.service';
import { AuthService } from '../auth/auth.service';
import { IIdentity } from '../Models/i-identity';
import { ICreateTaskRequest } from '../Models/icreate-task-request';
import { TasksService } from '../Services/tasks.service';
import { switchMap } from 'rxjs';
import { ITask } from '../Models/i-task';
import { IUpdateTaskRequest } from '../Models/iupdate-task-request';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  identities: Partial<IIdentity>[] = [];
  tasks: (ITask & { created: boolean })[][] = [];
  identities$ = this.identityService.identities$;
  userId: number | undefined;
  selectedIdentityIndex: number | undefined;
  editingIndex: number | undefined;
  menuOpen: boolean[] = [];
  isAddTaskModalOpen: boolean = false;
  addTaskForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private identityService: IdentitiesService,
    private authService: AuthService,
    private tasksService: TasksService
  ) {
    this.addTaskForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      cue: ['', Validators.required],
      craving: ['', Validators.required],
      response: ['', Validators.required],
      reward: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.authService.getUserId().subscribe(
      (id) => {
        this.userId = id;
        this.identityService.loadIdentities();
        this.identityService.identities$.subscribe((identities) => {
          if (identities) {
            this.populateIdentities(identities);
          }
        });
      },
      (error) => {
        console.error('Error getting user ID', error);
      }
    );

    setInterval(() => {
      const current = new Date();
      if (current.getHours() !== 0 || current.getMinutes() !== 0) {
        console.log("esco dall'interval");
        return;
      }
      console.log('faccio la chimaata');
      if (this.identities && this.identities.length > 0) {
        this.identities.forEach((identity, index) => {
          if (identity.id) {
            this.tasksService.loadTasksByIdentity(identity.id);
          }
        });
      }
    }, 40000);
  }

  populateIdentities(identities: IIdentity[]): void {
    this.identities = identities.slice(0, 3);
    this.identities.forEach((identity, index) => {
      if (identity.id) {
        this.tasks[index] = [];
        this.tasksService
          .getTasksByIdentityId(identity.id)
          .subscribe((tasks) => {
            this.tasks[index] = tasks.map((task) => ({
              ...task,
              created: true,
            }));
          });
      }
    });
  }

  onSubmitIdentity(index: number): void {
    if (!this.userId) {
      console.error('User ID is not defined');
      return;
    }

    const identityData = this.identities[index];

    if (identityData.id) {
      this.identityService
        .updateIdentity(identityData.id, identityData)
        .subscribe(
          (response) => {
            console.log('Identity updated successfully', response);
            this.identities[index] = response;
            this.selectedIdentityIndex = undefined;
            this.editingIndex = undefined;
          },
          (error) => {
            console.error('Error updating identity', error);
          }
        );
    } else {
      this.identityService.createIdentity(this.userId, identityData).subscribe(
        (response) => {
          console.log('Identity created successfully', response);
          this.identities[index] = response;
          this.tasks[index] = [
            {
              id: 0,
              title: '',
              description: '',
              cue: '',
              craving: '',
              response: '',
              reward: '',
              completed: false,
              createdAt: new Date(),
              identityId: response.id,
              created: false,
            },
          ];
        },
        (error) => {
          console.error('Error creating identity', error);
        }
      );
    }
  }

  openAddTaskModal(index: number): void {
    this.selectedIdentityIndex = index;
    this.isAddTaskModalOpen = true;
  }

  closeAddTaskModal(): void {
    this.isAddTaskModalOpen = false;
    this.addTaskForm.reset();
  }

  onSubmitTask(): void {
    if (this.selectedIdentityIndex === undefined) {
      console.error('Selected identity index is not defined');
      return;
    }

    const identity = this.identities[this.selectedIdentityIndex];
    const taskData = this.addTaskForm.value;

    if (!identity.id) {
      console.error('Identity ID is not defined');
      return;
    }

    const createTaskRequest: ICreateTaskRequest = {
      title: taskData.title,
      description: taskData.description,
      cue: taskData.cue,
      craving: taskData.craving,
      response: taskData.response,
      reward: taskData.reward,
    };

    this.tasksService.createTask(identity.id, createTaskRequest).subscribe(
      (response) => {
        console.log('Task created successfully', response);
        this.tasks[this.selectedIdentityIndex!].push({
          ...response,
          created: true,
        });
      },
      (error) => {
        console.error('Error creating task', error);
      }
    );
    this.closeAddTaskModal();
  }

  toggleCompleteTask(identityIndex: number, taskIndex: number): void {
    const task = this.tasks[identityIndex][taskIndex];
    this.tasksService.completeTask(task.id!).subscribe(
      (response) => {
        console.log('Task completion toggled successfully', response);
        this.tasks[identityIndex][taskIndex] = { ...response, created: true };
      },
      (error) => {
        console.error('Error toggling task completion', error);
      }
    );
  }

  editIdentity(index: number): void {
    this.editingIndex = index;
    const identity = this.identities[index];
    this.selectedIdentityIndex = index;
  }

  deleteIdentity(id: number): void {
    this.identityService.deleteIdentity(id).subscribe(
      () => {
        console.log('Identity deleted successfully');
        this.ngOnInit();
      },
      (error) => {
        console.error('Error deleting identity', error);
      }
    );
  }

  toggleMenu(index: number): void {
    this.menuOpen[index] = !this.menuOpen[index];
  }
}
