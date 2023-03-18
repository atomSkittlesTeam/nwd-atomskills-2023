import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskForRepairsComponent } from './task-for-repairs.component';

describe('TaskForRepairsComponent', () => {
  let component: TaskForRepairsComponent;
  let fixture: ComponentFixture<TaskForRepairsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TaskForRepairsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TaskForRepairsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
