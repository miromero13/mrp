import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Enterprise } from '../models/enterprise.model';
import { ENV_CONFIG } from '../config/env-config';

@Injectable({
  providedIn: 'root'
})
export class EnterpriseService {
  private apiUrl = (ENV_CONFIG as any).apiUrl ? `${(ENV_CONFIG as any).apiUrl}/api/enterprises` : 'http://localhost:8090/api/enterprises';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Enterprise[]> {
    return this.http.get<Enterprise[]>(this.apiUrl);
  }
}
