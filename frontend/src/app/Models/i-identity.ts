import { ITask } from './i-task';
import { IUser } from './i-user';

export interface IIdentity {
  id?: number; // Facoltativo, poiché generato automaticamente
  title: string;
  description: string;
  createdAt?: Date; // Facoltativo, poiché generato automaticamente
  user?: IUser; // Facoltativo, poiché potrebbe essere associato dopo la creazione
  tasks?: ITask[]; // Facoltativo, poiché un'identità potrebbe non avere task inizialmente
}
