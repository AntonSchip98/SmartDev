export interface IUser {
  id: number;
  username: string;
  email: string;
  avatar?: string;
  identities?: number[];
  password?: string;
  token?: string; // Questo è opzionale, aggiungilo solo se presente
}
