import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { App } from './app';
import {RouterOutlet} from '@angular/router';

@NgModule({
  declarations: [
    App
  ],
  imports: [
    BrowserModule,
    RouterOutlet
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
  ],
  bootstrap: [App]
})
export class AppModule { }
