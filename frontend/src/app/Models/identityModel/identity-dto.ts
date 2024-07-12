export interface IdentityDto {
  id: number;
  title: string;
  description: string;
  createdAt: string; // Use string to represent ISO date
  userId: number;
  tasks: number[];
}
