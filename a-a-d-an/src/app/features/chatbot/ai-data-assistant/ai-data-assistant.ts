import { Component, OnInit, signal, inject, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ChatAskResponse, ChatSession } from '../../../shared/models/auth.model';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../shared/models/user.model';

declare var Plotly: any;

interface ChatMessage {
  role: 'user' | 'bot';
  content: string;
  plotlyJson?: string;
  chartRendered?: boolean;
}

const SUGGESTIONS_CUSTOMER = [
  'Show my recent orders',
  'How much have I spent this year?',
  'Show my reviews',
  'What products have I purchased?',
  'Show my order status',
];

const SUGGESTIONS_CORPORATE = [
  'Show sales by category',
  'Weekly revenue chart',
  'Top products performance',
  'Find negative reviews',
  'Customer distribution',
];

const SUGGESTIONS_ADMIN = [
  'Total platform revenue',
  'Show all stores comparison',
  'Top selling products',
  'Pending orders count',
  'User growth overview',
];

@Component({
  selector: 'app-ai-data-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-data-assistant.html',
  styleUrl: './ai-data-assistant.css',
})
export class AiDataAssistantComponent implements OnInit, AfterViewChecked {
  @ViewChild('messagesEnd') private messagesEnd!: ElementRef;
  @ViewChild('chatMessages') private chatMessages!: ElementRef;

  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private readonly API = 'http://localhost:8080/api/chat';

  private role = this.auth.getRole();

  suggestions = this.role === UserRole.CUSTOMER
    ? SUGGESTIONS_CUSTOMER
    : this.role === UserRole.ADMIN
      ? SUGGESTIONS_ADMIN
      : SUGGESTIONS_CORPORATE;

  private welcomeMessage = this.role === UserRole.CUSTOMER
    ? "Hello! I'm your personal AI assistant.\n\nI can help you explore your orders, spending, and reviews. Try asking:\n• \"Show my recent orders\"\n• \"How much have I spent this year?\"\n• \"Show my reviews\""
    : "Hello! I'm your AI data assistant with real-time visualization capabilities!\n\nI can analyze your data and create charts instantly. Try asking:\n• \"Show sales by category\"\n• \"Weekly revenue chart\"\n• \"Top products performance\"";

  sessionId = signal<string | null>(null);
  messages = signal<ChatMessage[]>([
    { role: 'bot', content: this.welcomeMessage },
  ]);
  input = '';
  loading = signal(false);

  ngOnInit() {
    this.http.post<ChatSession>(`${this.API}/sessions`, { title: 'New Chat' }).subscribe({
      next: s => this.sessionId.set(s.sessionId),
      error: () => {
        this.messages.update(m => [...m, {
          role: 'bot',
          content: '⚠️ Backend service is not reachable. Please make sure the Spring Boot server is running on port 8080, then refresh the page.',
        }]);
      },
    });
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
    this.renderPendingCharts();
  }

  send(text?: string) {
    const msg = (text ?? this.input).trim();
    if (!msg) return;
    const sid = this.sessionId();
    if (!sid) {
      this.messages.update(m => [...m, {
        role: 'bot',
        content: '⚠️ Session not ready. Please refresh the page and make sure the server is running.',
      }]);
      return;
    }
    this.messages.update(m => [...m, { role: 'user', content: msg }]);
    this.input = '';
    this.loading.set(true);

    this.http.post<ChatAskResponse>(`${this.API}/ask`, { sessionId: sid, question: msg }).subscribe({
      next: res => {
        const botMsg: ChatMessage = {
          role: 'bot',
          content: res.answer,
          plotlyJson: res.hasVisualization && res.visualizationCode ? res.visualizationCode : undefined,
          chartRendered: false,
        };
        this.messages.update(m => [...m, botMsg]);
        this.loading.set(false);
      },
      error: () => {
        this.messages.update(m => [...m, { role: 'bot', content: 'An error occurred, please try again.' }]);
        this.loading.set(false);
      },
    });
  }

  private renderPendingCharts() {
    if (typeof Plotly === 'undefined') return;
    const container = this.chatMessages?.nativeElement;
    if (!container) return;

    const chartDivs = container.querySelectorAll('.plotly-chart[data-json]:not([data-rendered])');
    chartDivs.forEach((div: HTMLElement) => {
      try {
        const jsonStr = div.getAttribute('data-json');
        if (!jsonStr) return;
        const figure = JSON.parse(jsonStr);
        figure.layout = figure.layout || {};
        figure.layout.paper_bgcolor = '#080818';
        figure.layout.plot_bgcolor = '#080818';
        figure.layout.font = { color: '#e2e8f0' };
        figure.layout.margin = { t: 40, r: 20, b: 60, l: 60 };
        Plotly.newPlot(div, figure.data, figure.layout, { responsive: true, displayModeBar: false });
        div.setAttribute('data-rendered', 'true');
      } catch {}
    });
  }

  private scrollToBottom() {
    try { this.messagesEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' }); } catch {}
  }
}
