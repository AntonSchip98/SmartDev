import { Component, OnInit } from '@angular/core';
import { IdentitiesService } from '../Services/identities.service';
import { AuthService } from '../auth/auth.service';
import { IIdentity } from '../Models/i-identity';
import { ICreateTaskRequest } from '../Models/icreate-task-request';
import { TasksService } from '../Services/tasks.service';
import { switchMap } from 'rxjs';
import { ITask } from '../Models/i-task';
import { IUpdateTaskRequest } from '../Models/iupdate-task-request';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  identities: Partial<IIdentity>[] = [
    { title: '', description: '' },
    { title: '', description: '' },
    { title: '', description: '' },
  ];
  tasks: (ITask & { created: boolean })[][] = [[], [], []];
  identities$ = this.identityService.identities$;
  userId: number | undefined;
  selectedIdentityId: number | undefined;
  editingIndex: number | undefined;
  menuOpen: boolean[] = [false, false, false];

  constructor(
    private identityService: IdentitiesService,
    private authService: AuthService,
    private tasksService: TasksService
  ) {}

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

    // Aggiorna le task periodicamente per riflettere i cambiamenti dal backend
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
    }, 40000); // Esegui ogni minuto
  }

  populateIdentities(identities: IIdentity[]): void {
    this.identities = [
      { title: '', description: '' },
      { title: '', description: '' },
      { title: '', description: '' },
    ];

    identities.forEach((identity, index) => {
      if (index < 3) {
        this.identities[index] = identity;
        this.tasksService.loadTasksByIdentity(identity.id!);
        this.tasksService.tasks$.subscribe((tasks) => {
          if (tasks) {
            this.tasks[index] = tasks.map((task) => ({
              ...task,
              created: true,
            }));
          }
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
            this.selectedIdentityId = undefined;
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
              title: '',
              description: '',
              cue: '',
              craving: '',
              response: '',
              reward: '',
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

  addTaskForm(identityIndex: number): void {
    this.tasks[identityIndex].push({
      title: '',
      description: '',
      cue: '',
      craving: '',
      response: '',
      reward: '',
      created: false,
    });
  }

  onSubmitTask(identityIndex: number, taskIndex: number): void {
    const identity = this.identities[identityIndex];
    const taskData = this.tasks[identityIndex][taskIndex];

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
        this.tasks[identityIndex][taskIndex] = { ...response, created: true };
      },
      (error) => {
        console.error('Error creating task', error);
      }
    );
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
    this.selectedIdentityId = identity.id;
  }

  deleteIdentity(id: number): void {
    this.identityService.deleteIdentity(id).subscribe(
      () => {
        console.log('Identity deleted successfully');
        this.ngOnInit(); // Ricarica le identità dopo l'eliminazione
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
