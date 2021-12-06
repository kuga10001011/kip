import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from "@angular/router";
import { MaterialModule } from "./material.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { FlexLayoutModule } from "@angular/flex-layout";

import { ValueComponent } from './components/datastore/value/value.component';
import { DatastoreComponent } from './components/datastore/datastore.component';
import { UserComponent } from './components/user/user.component';
import { FieldComponent } from './components/datastore/field/field.component';
import { UserManageComponent } from './components/user/user-manage/user-manage.component';
import { DatastoreViewComponent } from './components/datastore/datastore-view/datastore-view.component';
import { HomeComponent } from './components/home/home.component';
import { RowManageComponent } from './components/datastore/row/row-manage/row-manage.component';
import { DatastoreManageComponent } from './components/datastore/datastore-manage/datastore-manage.component';
import { ViewManageComponent } from './components/datastore/view/view-manage/view-manage.component';
import { ViewComponent } from "./components/datastore/view/view.component";
import { DragDropModule } from "@angular/cdk/drag-drop";

const routes: Routes = [
  { path: 'row/manage', component: RowManageComponent },
  { path: 'datastore', component: DatastoreComponent },
  { path: 'datastore/view', component: DatastoreViewComponent },
  { path: 'datastore/manage', component: DatastoreManageComponent },
  { path: 'user', component: UserComponent },
  { path: 'user/manage', component: UserManageComponent },
  { path: 'home', component: HomeComponent },
  { path: 'view/manage', component: ViewManageComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    RowManageComponent,
    ValueComponent,
    DatastoreComponent,
    UserComponent,
    FieldComponent,
    UserManageComponent,
    DatastoreViewComponent,
    HomeComponent,
    DatastoreManageComponent,
    ViewComponent,
    ViewManageComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(routes),
    MaterialModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    FlexLayoutModule,
    DragDropModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
