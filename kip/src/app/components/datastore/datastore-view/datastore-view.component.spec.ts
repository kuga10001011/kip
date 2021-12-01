import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatastoreViewComponent } from './datastore-view.component';

describe('DatastoreViewComponent', () => {
  let component: DatastoreViewComponent;
  let fixture: ComponentFixture<DatastoreViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatastoreViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatastoreViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
