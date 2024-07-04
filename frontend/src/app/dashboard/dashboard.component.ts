import { Component } from '@angular/core';
import { IdentityDto } from '../Models/identityModel/identity-dto';
import { TaskDto } from '../Models/taskModel/task-dto';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IdentityService } from '../Services/identity.service';
import { AuthService } from '../auth/auth.service';
import { TaskService } from '../Services/task.service';
import { UserService } from '../Services/comunication.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  identities: IdentityDto[] = [];
  tasks: TaskDto[][] = [];
  isAddTaskModalOpen: boolean = false;
  addTaskForm: FormGroup;
  selectedIdentityIndex: number | null = null;

  constructor(
    private identityService: IdentityService,
    private taskService: TaskService,
    private authService: AuthService,
    private communicationService: UserService,
    private fb: FormBuilder
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
    this.loadIdentities();

    this.communicationService.identityAdded$.subscribe(() => {
      this.loadIdentities(); // Reload identities when a new one is added
    });

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
            this.taskService.getAllTasksByIdentity(identity.id);
          }
        });
      }
    }, 40000);
  }

  loadIdentities(): void {
    this.identityService.getAllIdentitiesByUser().subscribe((identities) => {
      this.identities = identities;
      this.loadTasks();
    });
  }

  loadTasks(): void {
    this.tasks = [];
    this.identities.forEach((identity, index) => {
      this.taskService.getAllTasksByIdentity(identity.id).subscribe((tasks) => {
        this.tasks[index] = tasks;
      });
    });
  }

  toggleCompleteTask(identityIndex: number, taskIndex: number): void {
    const task = this.tasks[identityIndex][taskIndex];
    task.completed = !task.completed;
    this.taskService.updateTask(task.id, task).subscribe(() => {
      // Update the task list if necessary
    });
  }

  openAddTaskModal(identityIndex: number): void {
    this.isAddTaskModalOpen = true;
    this.selectedIdentityIndex = identityIndex;
  }

  closeAddTaskModal(): void {
    this.isAddTaskModalOpen = false;
    this.addTaskForm.reset();
    this.selectedIdentityIndex = null;
  }

  onSubmitTask(): void {
    if (this.addTaskForm.valid && this.selectedIdentityIndex !== null) {
      const identityId = this.identities[this.selectedIdentityIndex].id;
      const taskData: TaskDto = {
        ...this.addTaskForm.value,
        identityId,
        completed: false,
        createdAt: new Date().toISOString(),
      };

      this.taskService.createTask(identityId, taskData).subscribe(() => {
        this.closeAddTaskModal();
        this.loadTasks(); // Reload tasks after adding a new one
      });
    }
  }
}
