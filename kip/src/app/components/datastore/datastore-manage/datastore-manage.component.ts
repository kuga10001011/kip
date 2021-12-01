import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { Field } from "../field/field.model";
import { DatastoreService } from "../datastore.service";
import { TokenStorageService } from "../../user/auth/token-storage.service";
import { ActivatedRoute, Router } from "@angular/router";
import { KipHelperData } from "../../../shared/kip-helper-data";
import { Datastore } from "../datastore.model";

@Component({
  selector: 'app-datastore-manage',
  templateUrl: './datastore-manage.component.html',
  styleUrls: ['./datastore-manage.component.css']
})
export class DatastoreManageComponent implements OnInit, AfterViewInit {

  datastoreForm!: FormGroup;

  datastore!: Datastore;
  datastoreLoaded: boolean = false;

  allowedTypesLoaded: boolean = false;
  allowedTypes: string[] = [];
  selectedFieldType: string = '';

  enumVals: string[] = [];
  enumValsSet: Set<string> = new Set<string>();
  enumsDatasource: MatTableDataSource<string> = new MatTableDataSource<string>(this.enumVals);

  fields: Field[] = [];
  fieldSet: Set<string> = new Set<string>();
  fieldsDatasource: MatTableDataSource<Field> = new MatTableDataSource<Field>(this.fields);
  fieldsToUpdate: Map<number, Field> = new Map<number, Field>();
  fieldsToAdd: Set<string> = new Set<string>();
  fieldsToDelete: Set<number> = new Set<number>();

  tableDataComponents: Map<any, Set<any>> = new Map<any, Set<any>>();
  formComponents: Map<string, any> = new Map<string, any>();
  formComponentsRequired: Set<string> = new Set<string>();

  currentField?: Field;

  mode: 'add' | 'edit' = 'add';

  @ViewChildren('enumTable')
  enumTable!: QueryList<MatTable<string>>;

  @ViewChildren('fieldsTable')
  fieldsTable!: QueryList<MatTable<Field>>;

  @ViewChild('field_name')
  fieldNameInput!: ElementRef;

  @ViewChild('name')
  nameInput!: ElementRef

  @ViewChild('enum_value')
  enumInput!: ElementRef

  constructor(private datastoreService: DatastoreService, private tokenStorage: TokenStorageService, private router: Router, private activatedRoute: ActivatedRoute) {
    this.initializeForm();
    this.loadTypes();
    this.activatedRoute.queryParams.subscribe(params => {
      this.mode = params['mode'];
      if (this.mode == 'edit') {
        if (params['datastoreId']) {
          this.loadDatastore(params['datastoreId'])
        }
      }
    });
  }

  initializeForm(): void {
    this.formComponentsRequired.add('name');
    KipHelperData.addKeysWithValue(this.formComponents, String, 'name', 'field_name', 'field_type', 'enum_value');
    KipHelperData.addKeysWithValue(this.formComponents, Boolean, 'field_required');
    this.datastoreForm = new FormGroup({});
    this.formComponents.forEach((componentType: any, componentName: string) => {
      this.datastoreForm.addControl(componentName, new FormControl(KipHelperData.getDefaultInitialFormValue(componentType)));
      if (this.formComponentsRequired.has(componentName)) {
        this.datastoreForm.get(componentName)?.addValidators(Validators.required);
      }
    });
  }

  addTableComponents(table: MatTable<any>, ...components: any[]) {
    if (table && !this.tableDataComponents.has(table)) {
      this.tableDataComponents.set(table, new Set<any>());
      components.forEach((component: any) => {
        this.tableDataComponents.get(table)!.add(component);
      });
    }
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.nameInput.nativeElement.focus();
    this.fieldsTable.changes.subscribe(() => {
      if (this.fieldsTable.get(0)) {
        this.addTableComponents(this.fieldsTable.get(0)!, this.fields, this.fieldSet);
        this.resetTableComponents(this.fieldsTable.get(0)!, false);
      }
    });
    this.enumTable.changes.subscribe(() => {
      if (this.enumTable.get(0)) {
        this.addTableComponents(this.enumTable.get(0)!, this.enumVals, this.enumValsSet);
      }
    });
    this.fieldsTable.get(0)?.renderRows();
  }

  loadTypes(): void {
    this.datastoreService.getTypes().subscribe(
      data => {
        this.allowedTypes = data;
      },
      error => {
        console.log(error);
      },
      () => {
        this.allowedTypesLoaded = true;
      }
    );
  }

  loadDatastore(datastoreId: number): void {
    this.datastoreService.get(datastoreId).subscribe(
      data => {
        this.datastore = new Datastore().deserialize(data);
        this.datastore.fields.forEach((field: Field) => {
          this.fieldSet.add(JSON.stringify(field));
          this.fields.push(field);
        });
      },
      error => {
        console.log(error);
      },
      () => {
        this.datastoreForm.patchValue({'name': this.datastore.name});
        this.datastoreLoaded = true;
      }
    );
  }

  setSelectedFieldType(selectedFieldType: string) {
    this.selectedFieldType = selectedFieldType;
    if (this.enumTable.get(0)) {
      if (this.selectedFieldType == 'ENUM') {
        this.addTableComponents(this.enumTable.get(0)!, this.enumVals, this.enumValsSet);
      } else {
        this.tableDataComponents.delete(this.enumTable.get(0));
      }
    }
  }

  addEnumVal() {
    let enumVal = this.datastoreForm.get('enum_value')?.value;
    if (enumVal && !this.enumValsSet.has(enumVal)) {
      this.enumValsSet.add(enumVal)
      this.enumVals.push(enumVal);
      this.enumTable.get(0)!.renderRows();
      this.resetForm('enum_value');
      this.enumInput.nativeElement.focus();
    }
  }

  deleteEnumVal(enumValToDelete: string) {
    if (this.enumValsSet.has(enumValToDelete)) {
      this.enumVals.forEach((enumVal, index) => {
        if (enumVal == enumValToDelete) {
          this.enumValsSet.delete(enumValToDelete);
          this.enumVals.splice(index, 1);
          this.enumTable.get(0)!.renderRows();
        }
      });
    }
  }

  addField(): void {
    let field_name = this.datastoreForm.get('field_name')?.value;
    let field_type = this.datastoreForm.get('field_type')?.value;
    let field_required = this.datastoreForm.get('field_required')?.value;

    if (field_name && field_type) {
      const field = new Field().set(field_name, field_type, field_required);
      if (this.currentField && this.currentField.id) {
        field.id = this.currentField.id;
        this.fieldsToUpdate.set(this.currentField.id, field);
        this.deleteField(this.currentField, this.fieldsTable.get(0)!, true);
        this.currentField = undefined;
      }
      else {
        this.fieldsToAdd.add(JSON.stringify(field));
      }

      if (field_type == 'ENUM') {
        field.allowedValues = new Set<string>(this.enumValsSet);
      }
      let fieldDetailString = JSON.stringify(field);
      if (!this.fieldSet.has(fieldDetailString)) {
        this.fieldSet.add(fieldDetailString);
        this.fields.push(field);
        this.resetTableComponents(this.fieldsTable.get(0)!, false);
        if (field_type == 'ENUM') {
          this.resetTableComponents(this.enumTable.get(0)!, true);
        }
      }
      this.resetForm('field_name', 'field_type', 'field_required');
      this.fieldNameInput.nativeElement.focus();
    }
  }

  editField(field: Field) {
    this.currentField = field;
    this.datastoreForm.patchValue({'field_name': field.name});
    this.datastoreForm.patchValue({'field_type': field.type});
    this.datastoreForm.patchValue({'field_required': field.required});
    if (field.type == 'ENUM') {
      this.enumValsSet = field.allowedValues;
      this.enumVals = Array.from(this.enumValsSet);
      this.enumTable.get(0)!.renderRows();
    }
  }

  resetForm(...formComponentNames: string[]): void {
    formComponentNames.forEach((formComponentName: string) => {
      this.datastoreForm.patchValue({[formComponentName]: KipHelperData.getDefaultInitialFormValue(this.formComponents.get(formComponentName))})
    });
  }

  resetTableComponents(table: MatTable<any>, resetAll: boolean, ...tableComponents: any[]): void {
    if (this.tableDataComponents.has(table)) {
      if ((!tableComponents || tableComponents.length == 0) && resetAll) {
        tableComponents = Array.from(this.tableDataComponents.get(table)!);
      }
      if (tableComponents) {
        tableComponents.forEach((tableComponent: any) => {
          KipHelperData.resetContainer(tableComponent);
        });
      }
    }
    table.renderRows();
  }

  deleteField(fieldToDelete: Field, table: MatTable<any>, update: boolean) {
    this.fields.forEach((field, index) => {
      if (field == fieldToDelete) {
        this.fieldSet.delete(JSON.stringify(fieldToDelete));
        this.fields.splice(index, 1);
        if (this.mode == 'edit') {
          if (this.fieldsToAdd.has(JSON.stringify(fieldToDelete))) {
            this.fieldsToAdd.delete(JSON.stringify(fieldToDelete));
          }
          if (fieldToDelete.id && !update) {
            this.fieldsToDelete.add(fieldToDelete.id);
          }
        }
        table.renderRows();
      }
    });
  }

  submit(): void {
    let name = this.datastoreForm.get('name')?.value;
    if (name && this.tokenStorage.getUser()) {
      switch(this.mode) {
        case 'add':
          this.addDatastore(name);
          break;
        case 'edit':
          this.updateDatastore(name);
          break;
      }
    }

  }

  addDatastore(name: string) {
    this.datastoreService.add(name, this.fields, this.tokenStorage.getUser().id).subscribe(
      () => {
        this.router.navigate(['/datastore']);
      },
      error => {
        console.log(error);
      }
    );
  }

  updateDatastore(name: string) {
    const fieldsToAdd = new Set<Field>();
    this.fields.forEach((field: Field) => {
      if (this.fieldsToAdd.has(JSON.stringify(field))) {
        fieldsToAdd.add(field);
      }
    });
    this.datastoreService.update(this.datastore.id!, this.tokenStorage.getUser().id, fieldsToAdd, this.fieldsToDelete, this.fieldsToUpdate, name).subscribe(
      () => {
        this.router.navigate(['/datastore']);
      },
      error => {
        console.log(error);
      }
    );
  }

}
