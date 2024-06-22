import { IIdentity } from './i-identity';

export interface IUser {
  id?: number; // Facoltativo, poiché generato automaticamente
  username: string;
  email: string;
  password?: string;
  avatar?: string; // Facoltativo, poiché un utente potrebbe non avere un avatar inizialmente
  identities?: IIdentity[]; // Facoltativo, poiché un utente potrebbe non avere identità inizialmente
}
