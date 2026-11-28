import test from 'node:test';
import assert from 'node:assert/strict';
import {
  formatDate,
  priorityLabel,
  statusLabel
} from '../.tmp/helpers/shared/ticket-format.js';

test('maps every ticket status to a Spanish label', () => {
  assert.equal(statusLabel('OPEN'), 'Abierta');
  assert.equal(statusLabel('IN_PROGRESS'), 'En progreso');
  assert.equal(statusLabel('RESOLVED'), 'Resuelta');
});

test('maps every priority to a Spanish label', () => {
  assert.equal(priorityLabel('LOW'), 'Baja');
  assert.equal(priorityLabel('MEDIUM'), 'Media');
  assert.equal(priorityLabel('HIGH'), 'Alta');
  assert.equal(priorityLabel('URGENT'), 'Urgente');
});

test('formats a valid ISO date using the Spanish locale', () => {
  const formatted = formatDate('2026-11-28T08:00:00Z');
  assert.match(formatted, /2026/);
  assert.doesNotMatch(formatted, /Invalid Date/);
});

test('returns a fallback for an invalid date', () => {
  assert.equal(formatDate('not-a-date'), 'Fecha no disponible');
});
