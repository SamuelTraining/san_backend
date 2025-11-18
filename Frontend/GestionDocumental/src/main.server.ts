import 'zone.js/node'; // 👈 Import obligatorio para SSR
import { bootstrapApplication, BootstrapContext } from '@angular/platform-browser';
import { App } from './app/app';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { importProvidersFrom } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { serverRoutes } from './app/app.routes.server';

// Export a bootstrap function that accepts a BootstrapContext. This is required
// for route extraction during the SSR build step. Do NOT call it here; the
// build/render tool will invoke the default export with the proper context.
const bootstrap = (context: BootstrapContext) =>
    bootstrapApplication(App, {
        providers: [
            // Configure server rendering with the server routes used during extraction
            provideServerRendering(withRoutes(serverRoutes)),
            provideRouter(routes),
            provideHttpClient(withFetch()),
            importProvidersFrom(FormsModule),
        ],
    }, context);

export default bootstrap;
