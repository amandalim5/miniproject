export interface Registration {
  userName: string;
  userEmail: string;
  userPassword: string;
  gender: string;
}

export interface Login {
  email: string;
  password: string;
}

export interface LoginResponse {
  result: boolean;
  message: string;
  timestamp: string;
  token: string;
}

export interface TokenResponse {
  result: boolean;
  useremail: string;
  newToken: string;
  username: string;
}

export interface ResetResponse {
  result: boolean;
  message: string;
}

export interface CheckEmailResponse {
  result: boolean;
}

export interface RegistrationResponse {
  result: boolean;
}

export interface GenericResponse {
  result: boolean;
  message: string;
}

export interface ProfileResponse {
  result: boolean;
  message: string;
  profileId: number;
  email: string;
  profileIsPublic: boolean;
  displayName: string;
  summary: string;
  birthday: number;
  birthmonth: number;
  birthyear: number;
  height: number;
  weight: number;
  isSmoking: string;
  postalCode: string;
  age: number;
  distance: number;
  token: string;
  mail: string;
  photo: string;
  photoUrl: string;
  liked: boolean;
}

export interface Profile {
  email: string;
  profileIsPublic: boolean;
  displayName: string;
  summary: string;
  birthday: number;
  birthmonth: number;
  birthyear: number;
  height: number;
  weight: number;
  isSmoking: string;
  postalCode: string;
  mail: string;
}

export interface DistanceResponse {
  status: string;
  distance: string;
}

export interface UserResponse {
  email: string;
  likes: number;
  checks: number;
}

export interface UploadResult {
  imageKey: string;
  image: string;
}

export interface ChatIdResponse {
  result: boolean;
  chatId: number;
  otherDisplayName: string;
  photoUrl: string;
}

export interface ChatMessage {
  message: string;
  author: string;
}
