import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { RowService } from "../row.service";
import { Row } from "../row.model";
import { Field } from "../../field/field.model";
import { DatastoreService } from "../../datastore.service";
import { FieldValue } from "../../field-value.model";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { Value } from "../../value/value.model";
import { Datastore } from "../../datastore.model";
import { Range } from "../../range.model";

@Component({
  selector: 'app-row-manage',
  templateUrl: './row-manage.component.html',
  styleUrls: ['./row-manage.component.css']
})
export class RowManageComponent implements OnInit {

  rowForm: FormGroup;
  mode!: 'add' | 'edit';
  row!: Row;
  datastore!: Datastore;
  dataLoaded: boolean = false;
  listItems: Map<string, Set<string>> = new Map<string, Set<string>>();
  listDatasources: Map<string, MatTableDataSource<string>> = new Map<string, MatTableDataSource<string>>();
  fieldValuesToUpdate: Map<number, Value> = new Map<number, Value>();
  fieldValuesToAdd: Set<FieldValue> = new Set<FieldValue>();
  fieldValuesToDelete: Set<number> = new Set<number>();
  originalValues: Map<number, FieldValue> = new Map<number, FieldValue>();

  constructor(private rowService: RowService, private datastoreService: DatastoreService, private activatedRoute: ActivatedRoute, private router: Router) {
    this.rowForm = new FormGroup({});
    this.activatedRoute.queryParams.subscribe(params => {
      this.mode = params['mode'];
      if (params['rowId']) {
        this.loadDatastore(params['datastoreId'], params['rowId']);
      }
      else {
        this.loadDatastore(params['datastoreId']);
      }
    });
  }

  ngOnInit(): void { }

  loadDatastore(datastoreId: number, rowId?: number): void {
    this.datastoreService.get(datastoreId).subscribe(
      datastore => {
        this.datastore = datastore;
      },
      error => {
        console.log(error);
      },
      () => {
        switch (this.mode) {
          case 'add':
            this.initializeForm();
            this.dataLoaded = true;
            break;
          case 'edit':
            this.loadRow(rowId!);
            break;
        }
      }
    );
  }

  loadRow(rowId: number): void {
    this.rowService.getById(rowId).subscribe(
      data => {
        this.row = new Row().deserialize(data);
      },
      error => {
        console.log(error);
      },
      () => {
        this.initializeForm(this.row.fieldValues);
        this.dataLoaded = true;
      }
    );
  }

  initializeForm(fieldValues?: Set<FieldValue>): void {
    this.datastore.fields.forEach((field: Field) => {
      const controlKey = field.name + field.id;
      switch (field.type) {
        case 'RANGE':
          this.rowForm.addControl(controlKey + '_floor', new FormControl());
          this.rowForm.addControl(controlKey + '_ceiling', new FormControl());
          break;
        default:
          this.rowForm.addControl(controlKey, new FormControl());
          break;
      }
      if (field.required) {
        switch (field.type) {
          case 'RANGE':
            this.rowForm.get(controlKey + '_floor')?.addValidators(Validators.required);
            this.rowForm.get(controlKey + '_ceiling')?.addValidators(Validators.required);
            break;
          default:
            this.rowForm.get(controlKey)?.addValidators(Validators.required);
            break;
        }
      }
      switch (field.type) {
        case 'LIST':
          this.listItems.set(controlKey, new Set<string>());
          this.listDatasources.set(controlKey, new MatTableDataSource<string>());
          break;
        case 'BOOLEAN':
          this.rowForm.patchValue({[controlKey]:false})
          break;
      }
    });
    if (fieldValues) {
      fieldValues.forEach((fieldValue: FieldValue) => {
        this.originalValues.set(fieldValue.field.id!, fieldValue);
        const controlKey = fieldValue.field.name + fieldValue.field.id;
        switch (fieldValue.field.type) {
          case 'RANGE':
            this.rowForm.patchValue({[controlKey + '_floor']: fieldValue.value.value.floor});
            this.rowForm.patchValue({[controlKey + '_ceiling']: fieldValue.value.value.ceiling});
            break;
          case 'LIST':
            fieldValue.value.value.forEach((item: string) => {
              this.listItems.get(controlKey)!.add(item);
            });
            this.listDatasources.set(controlKey, new MatTableDataSource<string>(Array.from(this.listItems.get(controlKey)!)));
            break;
          default:
            this.rowForm.patchValue({[controlKey]: fieldValue.value.value});
            break;
        }
      });
    }
  }

  addListItem(controlKey: string, table: MatTable<any>, input: any): void {
    const itemSet = this.listItems.get(controlKey);
    const value = this.rowForm.get(controlKey)?.value;
    if (value) {
      if (itemSet && !itemSet.has(value)) {
        itemSet.add(value);
        this.listDatasources.set(controlKey, new MatTableDataSource<string>(Array.from(itemSet)));
        this.reloadListTable(controlKey, table);
        this.rowForm.patchValue({[controlKey]:''});
        input.focus();
      }
    }
  }

  deleteListItem(controlKey: string, item: string, table: MatTable<any>, input: any) {
    const itemSet = this.listItems.get(controlKey);
    if (itemSet && itemSet.has(item)) {
      itemSet.delete(item);
      this.listDatasources.set(controlKey, new MatTableDataSource<string>(Array.from(itemSet)));
      this.reloadListTable(controlKey, table);
      input.focus();
    }
  }

  reloadListTable(controlKey: string, table: MatTable<any>) {
    this.listDatasources.set(controlKey, new MatTableDataSource<string>(Array.from(this.listItems.get(controlKey)!)));
    table.renderRows();
  }

  getDatasource(controlKey: string): MatTableDataSource<string> {
    return this.listDatasources.get(controlKey)!;
  }

  getRowSpan(field: Field): number {
    switch (field.type) {
      case 'LIST':
        return 2;
      default:
        return 1;
    }
  }

  getColSpan(field: Field): number {
    switch (field.type) {
      case 'URL':
        return 2;
      default:
        return 1;
    }
  }

  clear(field: Field): void {
    const controlKey = field.name + field.id;
    switch (field.type) {
      case 'LIST':
        this.listItems.get(controlKey)!.clear();
        this.listDatasources.set(controlKey, new MatTableDataSource<string>(Array.from(this.listItems.get(controlKey)!)));
        break;
      case 'RANGE':
        this.rowForm.setControl(controlKey + '_floor', new FormControl());
        this.rowForm.setControl(controlKey + '_ceiling', new FormControl());
        break;
      default:
        this.rowForm.setControl(controlKey, new FormControl());
        break;
    }
    if (this.originalValues.get(field.id!)) {
      this.fieldValuesToDelete.add(this.originalValues.get(field.id!)!.id!);
    }
  }

  submit(): void {
    this.datastore.fields.forEach((field: Field) => {
      const controlKey = field.name + field.id;
      let value;
      switch (field.type) {
        case 'LIST':
          value = new Value().set(Array.from(this.listItems.get(controlKey)!));
          break;
        case 'RANGE':
          const floor = this.rowForm.get(controlKey + '_floor')?.value;
          const ceiling = this.rowForm.get(controlKey + '_ceiling')?.value;
          if (floor && ceiling && floor <= ceiling) {
            value = new Value().set(new Range(floor, ceiling));
          }
          break;
        default:
          if (this.rowForm.get(controlKey)?.value || field.type == 'BOOLEAN') {
            value = new Value().set(this.rowForm.get(controlKey)?.value);
          }
          break;
      }
      if (value) {
        if (this.mode == 'add' || !this.originalValues.has(field.id!)) {
          this.fieldValuesToAdd.add(new FieldValue().set(field, value));
        }
        else if(this.mode == 'edit') {
          const originalValue: FieldValue = this.originalValues.get(field.id!)!;
          if (originalValue.value.value != value.value) {
            this.fieldValuesToUpdate.set(originalValue.id!, value);
            this.fieldValuesToDelete.delete(originalValue.id!);
          }
        }
      }
    });
    switch (this.mode) {
      case 'add':
        this.rowService.add(this.fieldValuesToAdd, this.datastore).subscribe(
          data => {
            console.log(data);
            console.log("successfully added");
          },
          error => {
            console.log(error);
          },
          () => {
            this.router.navigate(['/datastore/view'], {queryParams: {datastoreId: this.datastore.id}})
          }
        );
        break;
      case 'edit':
        this.rowService.update(this.row.id!, this.fieldValuesToAdd, this.fieldValuesToUpdate, this.fieldValuesToDelete).subscribe(
          data => {
            console.log(data);
            console.log("successfully updated");
          },
          error => {
            console.log(error);
          },
          () => {
            this.router.navigate(['/datastore/view'], {queryParams: {datastoreId: this.datastore.id}})
          }
        );
        break;
    }
  }

}
