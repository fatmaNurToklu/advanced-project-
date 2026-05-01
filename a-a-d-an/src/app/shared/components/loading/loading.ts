import { Component, Input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading.html',
  styleUrl: './loading.css',
})
export class LoadingComponent {
  @Input() set loadingMessage(val: string) { this.message.set(val); }
  message = signal('Loading...');
}
