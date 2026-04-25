import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { CreateUserFormValue, UpdateUserFormValue, UserListItem } from '../models/user.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly usersUrl = apiEndpoint(API_ROUTES.users.list);

  listUsers() {
    return this.http.get<ApiResponse<UserListItem[]>>(this.usersUrl).pipe(map((response) => response.data ?? []));
  }

  createUser(payload: CreateUserFormValue) {
    return this.http.post<ApiResponse<UserListItem>>(this.usersUrl, payload);
  }

  updateUser(id: string, payload: UpdateUserFormValue) {
    return this.http.put<ApiResponse<UserListItem>>(`${this.usersUrl}/${id}`, payload);
  }

  deleteUser(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.usersUrl}/${id}`);
  }
}
