import { IIdentity } from './i-identity';

export interface ITask {
  id?: number; // Facoltativo, poiché generato automaticamente
  title: string;
  description: string;
  cue: string;
  craving: string;
  response: string;
  reward: string;
  completed?: boolean; // Facoltativo, poiché un task potrebbe non essere completato inizialmente
  createdAt?: Date; // Facoltativo, poiché generato automaticamente
  identityId?: number; // Facoltativo, poiché potrebbe essere associato dopo la creazione
}
