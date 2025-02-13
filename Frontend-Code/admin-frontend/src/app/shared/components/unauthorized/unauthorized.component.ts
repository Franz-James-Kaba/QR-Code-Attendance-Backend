import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [RouterModule, ButtonComponent],
  template: './unauthorized.component.html',
})
export class UnauthorizedComponent {}
