export interface TaskDto {
  id: number;
  title: string;
  description: string;
  cue: string;
  craving: string;
  response: string;
  reward: string;
  completed: boolean;
  createdAt: string; // Use string to represent ISO date
  identityId: number;
}
