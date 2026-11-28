export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';

export interface Ticket {
  id: number;
  title: string;
  description: string;
  requesterEmail: string;
  priority: TicketPriority;
  status: TicketStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  requesterEmail: string;
  priority: TicketPriority;
}

export interface TicketPage {
  content: Ticket[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface TicketQuery {
  page: number;
  size: number;
  status?: TicketStatus;
  priority?: TicketPriority;
}
