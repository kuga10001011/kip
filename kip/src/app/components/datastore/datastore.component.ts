import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from "@angular/material/table";
import { Datastore } from "./datastore.model";
import { DatastoreService } from "./datastore.service";

@Component({
  selector: 'app-datastore',
  templateUrl: './datastore.component.html',
  styleUrls: ['./datastore.component.css']
})
export class DatastoreComponent implements OnInit {

  datastoresLoaded: boolean;
  datastores: Datastore[] = [];
  datasource: MatTableDataSource<Datastore> = new MatTableDataSource<Datastore>();

  constructor(private datastoreService: DatastoreService) {
    this.datastoresLoaded = false;
    this.loadDatastores();
  }

  ngOnInit(): void {
  }

  loadDatastores(): void {
    this.datastoreService.getAll().subscribe(
      data => {
        data.forEach((datastore) => {
          this.datastores.push(new Datastore().deserialize(datastore));
        })
        this.datasource = new MatTableDataSource<Datastore>(this.datastores);
      },
      error => {
        console.log(error);
      },
      () => {
        this.datastoresLoaded = true;
      }
    );
  }

  delete(datastore: Datastore) {
    this.datastoreService.delete(datastore).subscribe(
      () => {
        console.log('delete successful: ' + datastore.id);
      },
      error => {
        console.log(error);
      },
      () => {
        window.location.reload();
      }
    );
  }

}
