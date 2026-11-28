import type { TicketPriority, TicketStatus } from '../ticket.models.js';

const STATUS_LABELS: Record<TicketStatus, string> = {
  OPEN: 'Abierta',
  IN_PROGRESS: 'En progreso',
  RESOLVED: 'Resuelta'
};

const PRIORITY_LABELS: Record<TicketPriority, string> = {
  LOW: 'Baja',
  MEDIUM: 'Media',
  HIGH: 'Alta',
  URGENT: 'Urgente'
};

export function statusLabel(status: TicketStatus): string {
  return STATUS_LABELS[status];
}

export function priorityLabel(priority: TicketPriority): string {
  return PRIORITY_LABELS[priority];
}

export function formatDate(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return 'Fecha no disponible';
  }

  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(date);
}
