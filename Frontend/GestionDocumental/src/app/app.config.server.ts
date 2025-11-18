import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { appConfig } from './app.config';

// Note: server rendering (provideServerRendering) is configured in the
// server bootstrap (`src/main.server.ts`) to ensure the bootstrap function
// has the correct signature for route extraction. Keep this file as a
// simple merge of common app providers.
const serverConfig: ApplicationConfig = {
  providers: []
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
