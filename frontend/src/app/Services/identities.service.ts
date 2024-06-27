import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { IUser } from '../Models/i-user';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { IIdentity } from '../Models/i-identity';

@Injectable({
  providedIn: 'root',
})
export class IdentitiesService {
  // URL base per le API delle identità, definito nell'ambiente di sviluppo
  apiUrl: string = environment.identitiesUrl;

  // BehaviorSubject per mantenere lo stato delle identità
  identitiesSubject = new BehaviorSubject<IIdentity[] | null>([]);
  // Observable che espone il BehaviorSubject per permettere la sottoscrizione ai cambiamenti dello stato delle identità
  identities$ = this.identitiesSubject.asObservable();

  constructor(private http: HttpClient) {} // Iniezione del servizio HttpClient nel costruttore

  // Metodo per caricare tutte le identità dell'utente loggato
  loadIdentities(): void {
    this.http.get<IIdentity[]>(`${this.apiUrl}/user`).subscribe(
      (identities) => this.identitiesSubject.next(identities), // Aggiorna il BehaviorSubject con le identità caricate
      (error) => console.error('Error loading identities', error) // Aggiorna il BehaviorSubject con le identità caricate
    );
  }

  // Metodo per creare una nuova identità
  createIdentity(
    userId: number,
    identityData: Partial<IIdentity>
  ): Observable<IIdentity> {
    return this.http
      .post<IIdentity>(`${this.apiUrl}/${userId}`, identityData)
      .pipe(
        // Aggiorna il BehaviorSubject aggiungendo la nuova identità alla lista esistente
        tap((newIdentity: IIdentity) => {
          const currentIdentities = this.identitiesSubject.value ?? [];
          this.identitiesSubject.next([...currentIdentities, newIdentity]);
        })
      );
  }

  // Metodo per aggiornare un'identità esistente
  updateIdentity(
    id: number,
    identityData: Partial<IIdentity>
  ): Observable<IIdentity> {
    return this.http.put<IIdentity>(`${this.apiUrl}/${id}`, identityData).pipe(
      // Aggiorna il BehaviorSubject sostituendo l'identità aggiornata nella lista esistente
      tap((updatedIdentity: IIdentity) => {
        const currentIdentities =
          this.identitiesSubject.value?.map((identity) =>
            identity.id === id ? updatedIdentity : identity
          ) ?? [];
        this.identitiesSubject.next(currentIdentities);
      })
    );
  }

  // Metodo per eliminare un'identità
  deleteIdentity(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      // Aggiorna il BehaviorSubject rimuovendo l'identità eliminata dalla lista esistente
      tap(() => {
        const currentIdentities =
          this.identitiesSubject.value?.filter(
            (identity) => identity.id !== id
          ) ?? [];
        this.identitiesSubject.next(currentIdentities);
      })
    );
  }

  // Metodo per ottenere una singola identità per ID
  getIdentity(id: number): Observable<IIdentity> {
    return this.http.get<IIdentity>(`${this.apiUrl}/${id}`);
  }
}
