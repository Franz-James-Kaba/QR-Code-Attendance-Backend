import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';

import { routes } from './app.routes';
import { authReducer } from './core/store/states/auth/auth.reducer';
import { AuthEffects } from './core/store/states/auth/auth.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideAnimations(),
    provideStore({ auth: authReducer }),
    provideEffects(AuthEffects),
    provideStoreDevtools(),
    provideHttpClient(
      withInterceptors([
        authInterceptor
      ])
    ),
  ]
};
