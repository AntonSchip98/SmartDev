import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { IUser } from '../Models/i-user';

@Injectable({
  providedIn: 'root',
})
export class IdentitiesService {
  constructor(private http: HttpClient) {}

  apiUrl: string = environment.usersUrl;

  getById(id: number) {
    return this.http.get<IUser>(this.apiUrl + '/' + id);
  }
}
