import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductionTasksComponent } from './production-tasks.component';

describe('ProductionTasksComponent', () => {
  let component: ProductionTasksComponent;
  let fixture: ComponentFixture<ProductionTasksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductionTasksComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductionTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
