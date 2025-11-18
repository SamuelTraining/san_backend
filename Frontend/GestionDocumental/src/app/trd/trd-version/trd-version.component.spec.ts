import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrdVersionComponent } from './trd-version.component';
import { TrdVersionService } from './trd-version.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';

describe('TrdVersionComponent', () => {
  let component: TrdVersionComponent;
  let fixture: ComponentFixture<TrdVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TrdVersionComponent],
      imports: [HttpClientTestingModule, FormsModule],
      providers: [TrdVersionService]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TrdVersionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
