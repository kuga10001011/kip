import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { DatastoreService } from "../../datastore.service";
import { ActivatedRoute, Router } from "@angular/router";
import { Datastore } from "../../datastore.model";
import { Field } from "../../field/field.model";
import { CdkDragDrop, moveItemInArray, transferArrayItem } from "@angular/cdk/drag-drop";
import {ViewService} from "../view.service";

@Component({
  selector: 'app-view-manage',
  templateUrl: './view-manage.component.html',
  styleUrls: ['./view-manage.component.css']
})
export class ViewManageComponent implements OnInit {

  viewForm: FormGroup;

  datastoreLoaded: boolean = false;
  datastore!: Datastore;
  fields: Array<Field> = new Array<Field>();
  chosenFields: Array<Field> = new Array<Field>();

  mode: 'add' | 'edit' = 'add';

  constructor(private datastoreService: DatastoreService, private activatedRoute: ActivatedRoute, private viewService: ViewService, private router: Router) {
    this.activatedRoute.queryParams.subscribe(params => {
      this.mode = params['mode'];
      if (params['datastoreId']) {
        this.loadDatastore(params['datastoreId'])
      }
    });
    this.viewForm = new FormGroup({
      name: new FormControl('', [Validators.required])
    });
  }

  ngOnInit(): void {
  }

  onFieldDrop(event: CdkDragDrop<Field[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    }
    else {
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
    }
  }

  loadDatastore(datastoreId: number): void {
    this.datastoreService.get(datastoreId).subscribe(
      data => {
        this.datastore = new Datastore().deserialize(data);
        this.datastore.fields.forEach((field: Field) => {
          this.fields.push(field);
        });
      },
      error => {
        console.log(error);
      },
      () => {
        this.datastoreLoaded = true;
      }
    );
  }

  save(): void {
    const name = this.viewForm.get('name')?.value;
    if (this.chosenFields.length > 0 && name) {
      this.viewService.add(this.datastore.id!, name, this.chosenFields).subscribe(
        data => {
          console.log(data);
          console.log("successfully added")
        },
        error => {
          console.log(error);
        },
        () => {
          this.router.navigate(['/datastore/view'], {queryParams:{datastoreId: this.datastore.id}})
        }
      );
    }
  }

}
