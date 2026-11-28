import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { TicketApiService } from './ticket-api.service';
import {
  CreateTicketRequest,
  Ticket,
  TicketPriority,
  TicketStatus
} from './ticket.models';
import { formatDate, priorityLabel, statusLabel } from './shared/ticket-format';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private readonly api = inject(TicketApiService);
  private readonly pageSize = 10;

  readonly tickets = signal<Ticket[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly notice = signal<string | null>(null);
  readonly page = signal(0);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly statusFilter = signal('');
  readonly priorityFilter = signal('');

  readonly openCount = computed(
    () => this.tickets().filter((ticket) => ticket.status === 'OPEN').length
  );
  readonly urgentCount = computed(
    () => this.tickets().filter((ticket) => ticket.priority === 'URGENT').length
  );
  readonly resolvedCount = computed(
    () => this.tickets().filter((ticket) => ticket.status === 'RESOLVED').length
  );

  readonly priorities: TicketPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  readonly statuses: TicketStatus[] = ['OPEN', 'IN_PROGRESS', 'RESOLVED'];

  readonly form = new FormGroup({
    title: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/\S/), Validators.maxLength(120)]
    }),
    description: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/\S/), Validators.maxLength(2000)]
    }),
    requesterEmail: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(254)]
    }),
    priority: new FormControl<TicketPriority>('MEDIUM', { nonNullable: true })
  });

  constructor() {
    this.loadTickets();
  }

  loadTickets(): void {
    this.loading.set(true);
    this.error.set(null);

    const status = this.statusFilter();
    const priority = this.priorityFilter();

    this.api.list({
      page: this.page(),
      size: this.pageSize,
      ...(status ? { status: status as TicketStatus } : {}),
      ...(priority ? { priority: priority as TicketPriority } : {})
    }).subscribe({
      next: (result) => {
        this.tickets.set(result.content);
        this.totalElements.set(result.totalElements);
        this.totalPages.set(result.totalPages);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se han podido cargar las incidencias. Comprueba que la API está iniciada.');
        this.loading.set(false);
      }
    });
  }

  applyFilters(): void {
    this.page.set(0);
    this.loadTickets();
  }

  clearFilters(): void {
    this.statusFilter.set('');
    this.priorityFilter.set('');
    this.page.set(0);
    this.loadTickets();
  }

  createTicket(): void {
    this.notice.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: CreateTicketRequest = this.form.getRawValue();
    this.saving.set(true);
    this.api.create(request).subscribe({
      next: () => {
        this.form.reset({
          title: '',
          description: '',
          requesterEmail: '',
          priority: 'MEDIUM'
        });
        this.page.set(0);
        this.saving.set(false);
        this.notice.set('La incidencia se ha creado correctamente.');
        this.loadTickets();
      },
      error: () => {
        this.saving.set(false);
        this.error.set('No se ha podido crear la incidencia. Revisa los datos e inténtalo de nuevo.');
      }
    });
  }

  updateStatus(ticket: Ticket, value: string): void {
    if (!this.statuses.includes(value as TicketStatus) || ticket.status === value) {
      return;
    }

    this.api.changeStatus(ticket.id, value as TicketStatus).subscribe({
      next: (updated) => {
        this.tickets.update((items) => items.map((item) => item.id === updated.id ? updated : item));
        this.notice.set(`La incidencia #${ticket.id} se ha actualizado.`);
      },
      error: () => this.error.set(`No se pudo actualizar la incidencia #${ticket.id}.`)
    });
  }

  deleteTicket(ticket: Ticket): void {
    if (!window.confirm(`¿Eliminar la incidencia #${ticket.id}? Esta acción no se puede deshacer.`)) {
      return;
    }

    this.api.delete(ticket.id).subscribe({
      next: () => {
        this.notice.set(`La incidencia #${ticket.id} se ha eliminado.`);
        this.loadTickets();
      },
      error: () => this.error.set(`No se pudo eliminar la incidencia #${ticket.id}.`)
    });
  }

  goToPage(nextPage: number): void {
    if (nextPage < 0 || nextPage >= this.totalPages() || nextPage === this.page()) {
      return;
    }
    this.page.set(nextPage);
    this.loadTickets();
  }

  statusLabel = statusLabel;
  priorityLabel = priorityLabel;
  formatDate = formatDate;
}
