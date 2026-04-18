import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { APP_NAME } from './app/core/config/app.config';

document.title = APP_NAME;

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
