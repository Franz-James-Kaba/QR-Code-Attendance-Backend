import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  @Input() showMobileMenu = true;
  @Input() showSearch = true;
  @Input() showUserMenu = true;
  @Input() userAvatar = 'https://ui-avatars.com/api/?name=Admin+User';
  @Input() userName = 'Admin User';
  @Input() sidebarOpen = true;
  @Input() sidebarMinimized = false;

  @Output() toggleSidebar = new EventEmitter<void>();

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }
}
