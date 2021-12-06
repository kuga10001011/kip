import { Component, OnInit } from '@angular/core';
import { Datastore } from "../datastore.model";
import { DatastoreService } from "../datastore.service";
import {ActivatedRoute, Router} from "@angular/router";
import { Field } from "../field/field.model";
import { Row } from "../row/row.model";
import { MatTableDataSource } from "@angular/material/table";
import { RowService } from "../row/row.service";
import { FieldValue } from "../field-value.model";
import { View } from "../view/view.model";
import { TokenStorageService } from "../../user/auth/token-storage.service";
import { User } from "../../user/user.model";

@Component({
  selector: 'app-datastore-view',
  templateUrl: './datastore-view.component.html',
  styleUrls: ['./datastore-view.component.css']
})
export class DatastoreViewComponent implements OnInit {

  datastore!: Datastore;
  datastoreLoaded: boolean = false;
  columnHeaders: string[] = [];
  fieldNameToType: Map<string, string> = new Map<string, string>();
  rows: Row[] = [];
  rowValueMap: Map<Row, Map<string, FieldValue>> = new Map<Row, Map<string, FieldValue>>();
  datasource: MatTableDataSource<Row> = new MatTableDataSource<Row>(this.rows);
  views: Array<View> = new Array<View>();
  user: User = this.tokenStorage.getUser();

  constructor(private datastoreService: DatastoreService, private rowService: RowService, private activatedRoute: ActivatedRoute, private router: Router, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.loadDatastore(params['datastoreId']);
    });
  }

  getColumnValue(row: Row, columnHeader: string): FieldValue | undefined {
    if (this.rowValueMap.get(row)?.get(columnHeader)) {
      return this.rowValueMap.get(row)!.get(columnHeader);
    }
    return undefined;
  }

  loadDatastore(id: number): void {
    this.datastoreService.get(id).subscribe(
      data => {
        this.datastore = new Datastore().deserialize(data);
        if (this.datastore.fields) {
          this.datastore.fields.forEach((field: Field) => {
            this.columnHeaders.push(field.name);
            this.fieldNameToType.set(field.name, field.type);
            switch (field.type) {
              case 'URL':
              case 'LIST':
                break;
              case 'STRING':
                break;
              default:
                break;
            }
          });
          this.columnHeaders.push('actions');
        }
        if (this.datastore.rows) {
          this.datastore.rows.forEach((row: Row) => {
            this.rows.push(row);
            if (row.fieldValues) {
              this.rowValueMap.set(row, new Map<string, FieldValue>());
              row.fieldValues.forEach((fieldValue: FieldValue) => {
                this.rowValueMap.get(row)!.set(fieldValue.field.name, fieldValue);
              });
            }
          });
        }
        if (this.datastore.views) {
          this.datastore.views.forEach((view: View) => {
            if (view.userIds.has(this.user.id!)) {
              this.views.push(view);
            }
          });
        }
      },
      error => {
        console.log(error);
      },
      () => {
        this.datastoreLoaded = true;
      }
    );
  }

  delete(row: Row) {
    this.rowService.delete(row).subscribe(
      () => {
        console.log(row);
        console.log("successfully deleted");
      },
      error => {
        console.log(error);
      },
      () => {
        window.location.reload();
      }
    );
  }

  getStyleFromFieldName(fieldName: string): string {
    switch(this.fieldNameToType.get(fieldName)!) {
      case 'STRING':
      case 'LIST':
        return "width-500-px";
      case 'URL':
        return "width-700-px";
      default:
        return "width-200-px";
    }
  }

  setSelectedView(view: View): void {

  }

}
