import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateTicketRequest,
  Ticket,
  TicketPage,
  TicketQuery,
  TicketStatus
} from './ticket.models';

@Injectable({ providedIn: 'root' })
export class TicketApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/tickets';

  list(query: TicketQuery): Observable<TicketPage> {
    let params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size);

    if (query.status) {
      params = params.set('status', query.status);
    }
    if (query.priority) {
      params = params.set('priority', query.priority);
    }

    return this.http.get<TicketPage>(this.endpoint, { params });
  }

  create(request: CreateTicketRequest): Observable<Ticket> {
    return this.http.post<Ticket>(this.endpoint, request);
  }

  changeStatus(id: number, status: TicketStatus): Observable<Ticket> {
    return this.http.patch<Ticket>(`${this.endpoint}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
