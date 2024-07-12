import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { Observable, catchError, throwError } from 'rxjs';
import { CreateTaskDto } from '../Models/taskModel/create-task-dto';
import { TaskDto } from '../Models/taskModel/task-dto';
import { UpdateTaskDto } from '../Models/taskModel/update-task-dto';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private taskUrl = environment.taskUrl;

  constructor(private http: HttpClient, private authSvc: AuthService) {}

  createTask(identityId: number, taskData: CreateTaskDto): Observable<TaskDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
      'Content-Type': 'application/json',
    });
    return this.http
      .post<TaskDto>(`${this.taskUrl}/${identityId}`, taskData, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }
  // Method to get a task by its ID
  getTask(id: number): Observable<TaskDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http.get<TaskDto>(`${this.taskUrl}/${id}`, { headers }).pipe(
      catchError(this.handleError) // Handle errors
    );
  }

  // Method to update an existing task
  updateTask(id: number, taskData: UpdateTaskDto): Observable<TaskDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
      'Content-Type': 'application/json',
    });
    return this.http
      .put<TaskDto>(`${this.taskUrl}/${id}`, taskData, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to delete a task by its ID
  deleteTask(id: number): Observable<void> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http.delete<void>(`${this.taskUrl}/${id}`, { headers }).pipe(
      catchError(this.handleError) // Handle errors
    );
  }

  // Method to get all tasks associated with a specific identity
  getAllTasksByIdentity(identityId: number): Observable<TaskDto[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
    });
    return this.http
      .get<TaskDto[]>(`${this.taskUrl}/identity/${identityId}`, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Method to mark a task as complete
  completeTask(id: number): Observable<TaskDto> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authSvc.getAccessToken()}`,
      'Content-Type': 'application/json',
    });
    return this.http
      .patch<TaskDto>(`${this.taskUrl}/${id}/complete`, {}, { headers })
      .pipe(
        catchError(this.handleError) // Handle errors
      );
  }

  // Error handling method
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      switch (error.status) {
        case 400:
          errorMessage = 'Bad request';
          break;
        case 401:
          errorMessage = 'Unauthorized';
          break;
        case 403:
          errorMessage = 'Forbidden';
          break;
        case 404:
          errorMessage = 'Not found';
          break;
        case 500:
          errorMessage = 'Internal server error';
          break;
        default:
          if (error.error && error.error.message)
            errorMessage = error.error.message;
          break;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
