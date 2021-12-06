import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewManageComponent } from './view-manage.component';

describe('ViewManageComponent', () => {
  let component: ViewManageComponent;
  let fixture: ComponentFixture<ViewManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewManageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
