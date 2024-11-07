// login-page.component.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css'],
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule]
})
export class LoginPageComponent {
  email: string = '';
  password: string = '';
  showPassword: boolean = false;
  isLoading: boolean = false;
  errorMessage: string = '';

  onSubmit(): void {
    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter both email and password';
      setTimeout(() => {
      this.errorMessage = '';
      }, 3000); // Clear error message after 3 seconds
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // TODO: Implement actual authentication logic here
    // Example:
    // this.authService.login(this.email, this.password).subscribe({
    //   next: (response) => {
    //     // Handle successful login
    //   },
    //   error: (error) => {
    //     this.errorMessage = error.message;
    //     this.isLoading = false;
    //   }
    // });

    // Simulate API call
    setTimeout(() => {
      this.isLoading = false;
      // Add your login logic here
    }, 1000);
  }
}
