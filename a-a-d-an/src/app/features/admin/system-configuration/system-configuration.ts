import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { SystemConfig } from '../../../shared/models/user.model';

@Component({
  selector: 'app-system-configuration',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './system-configuration.html',
  styleUrl: './system-configuration.css',
})
export class SystemConfigurationComponent implements OnInit {
  private userService = inject(UserService);

  configs = signal<SystemConfig[]>([]);
  loading = signal(false);
  saved = signal(false);
  error = signal('');
  newKey = '';
  newValue = '';

  ngOnInit() { this.loadConfig(); }

  loadConfig() {
    this.loading.set(true);
    this.userService.getSystemConfig().subscribe({
      next: c => { this.configs.set(c); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  updateAll() {
    const data: Record<string, string> = {};
    this.configs().forEach(c => (data[c.configKey] = c.configValue));
    this.userService.updateSystemConfig(data).subscribe({
      next: () => { this.saved.set(true); setTimeout(() => this.saved.set(false), 3000); },
      error: () => this.error.set('Failed to save configuration.'),
    });
  }

  addKey() {
    if (!this.newKey.trim() || !this.newValue.trim()) return;
    const data: Record<string, string> = { [this.newKey]: this.newValue };
    this.userService.updateSystemConfig(data).subscribe(() => {
      this.newKey = '';
      this.newValue = '';
      this.loadConfig();
    });
  }

  deleteKey(key: string) {
    if (!confirm(`Delete config key "${key}"?`)) return;
    this.userService.deleteConfigKey(key).subscribe(() => this.loadConfig());
  }

  trackByKey(_: number, c: SystemConfig) { return c.configKey; }
}
