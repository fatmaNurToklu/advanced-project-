import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ChatAskResponse, ChatSession } from '../../../shared/models/auth.model';
import { PageResponse } from '../../../core/services/product.service';

@Component({
  selector: 'app-ai-insight-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-insight-dashboard.html',
  styleUrl: './ai-insight-dashboard.css',
})
export class AiInsightDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/chat';

  sessions = signal<ChatSession[]>([]);
  activeSessionId = signal<string | null>(null);
  question = '';
  answer = signal('');
  sqlQuery = signal('');
  loading = signal(false);

  ngOnInit() {
    this.http.get<PageResponse<ChatSession>>(`${this.API}/sessions`).subscribe({
      next: res => {
        this.sessions.set(res.content);
        if (res.content.length > 0) {
          this.activeSessionId.set(res.content[0].sessionId);
        } else {
          this.createSession();
        }
      },
      error: () => this.createSession(),
    });
  }

  createSession() {
    this.http.post<ChatSession>(`${this.API}/sessions`, { title: 'Analitik Sohbet' }).subscribe({
      next: s => {
        this.sessions.update(list => [s, ...list]);
        this.activeSessionId.set(s.sessionId);
      },
    });
  }

  askAI() {
    const sid = this.activeSessionId();
    if (!this.question.trim() || !sid) return;
    this.loading.set(true);
    this.answer.set('');
    this.sqlQuery.set('');
    this.http.post<ChatAskResponse>(`${this.API}/ask`, { sessionId: sid, question: this.question }).subscribe({
      next: res => {
        this.answer.set(res.answer);
        this.sqlQuery.set(res.sqlQuery ?? '');
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
