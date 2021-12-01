import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatastoreManageComponent } from './datastore-manage.component';

describe('DatastoreManageComponent', () => {
  let component: DatastoreManageComponent;
  let fixture: ComponentFixture<DatastoreManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatastoreManageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatastoreManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
