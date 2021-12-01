import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RowManageComponent } from './row-manage.component';

describe('JobListingManageComponent', () => {
  let component: RowManageComponent;
  let fixture: ComponentFixture<RowManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RowManageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RowManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
