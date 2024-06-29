import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { ITask } from '../Models/i-task';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ICreateTaskRequest } from '../Models/icreate-task-request';
import { IUpdateTaskRequest } from '../Models/iupdate-task-request';

@Injectable({
  providedIn: 'root',
})
export class TasksService {
  // URL base per le API delle identità, definito nell'ambiente di sviluppo
  apiUrl: string = environment.taskUrl;

  // BehaviorSubject per mantenere lo stato delle task
  tasksSubject = new BehaviorSubject<ITask[] | null>([]);
  // Observable che espone il BehaviorSubject per permettere la sottoscrizione ai cambiamenti dello stato delle task
  tasks$ = this.tasksSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Metodo per caricare tutte le task di una certa identità
  loadTasksByIdentity(identityId: number): void {
    this.http.get<ITask[]>(`${this.apiUrl}/identity/${identityId}`).subscribe(
      (tasks) => this.tasksSubject.next(tasks), // Aggiorna il BehaviorSubject con le task caricate
      (error) => console.error('Error loading tasks', error)
    );
  }

  // Metodo per creare una nuova task
  createTask(
    identityId: number,
    taskData: ICreateTaskRequest
  ): Observable<ITask> {
    return this.http.post<ITask>(`${this.apiUrl}/${identityId}`, taskData).pipe(
      // Aggiorna il BehaviorSubject aggiungendo la nuova task alla lista esistente
      tap((newTask: ITask) => {
        const currentTasks = this.tasksSubject.value ?? [];
        this.tasksSubject.next([...currentTasks, newTask]);
      })
    );
  }

  // Metodo per aggiornare una task esistente
  updateTask(id: number, taskData: IUpdateTaskRequest): Observable<ITask> {
    return this.http.put<ITask>(`${this.apiUrl}/${id}`, taskData).pipe(
      // Aggiorna il BehaviorSubject sostituendo la task aggiornata nella lista esistente
      tap((updatedTask: ITask) => {
        const currentTasks =
          this.tasksSubject.value?.map((task) =>
            task.id === id ? updatedTask : task
          ) ?? [];
        this.tasksSubject.next(currentTasks);
      })
    );
  }

  // Metodo per eliminare una task
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      // Aggiorna il BehaviorSubject rimuovendo la task eliminata dalla lista esistente
      tap(() => {
        const currentTasks =
          this.tasksSubject.value?.filter((task) => task.id !== id) ?? [];
        this.tasksSubject.next(currentTasks);
      })
    );
  }

  // Metodo per ottenere una singola task per ID
  getTask(id: number): Observable<ITask> {
    return this.http.get<ITask>(`${this.apiUrl}/${id}`);
  }

  // Metodo per completare una task
  completeTask(id: number): Observable<ITask> {
    return this.http.patch<ITask>(`${this.apiUrl}/${id}/complete`, {}).pipe(
      // Aggiorna il BehaviorSubject con la task completata
      tap((updatedTask: ITask) => {
        const currentTasks =
          this.tasksSubject.value?.map((task) =>
            task.id === id ? updatedTask : task
          ) ?? [];
        this.tasksSubject.next(currentTasks);
      })
    );
  }
  getTasksByIdentityId(identityId: number): Observable<ITask[]> {
    return this.http.get<ITask[]>(`${this.apiUrl}/identity/${identityId}`);
  }
}
