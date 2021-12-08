package org.kuga.kip.datastore.row;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.DatastoreController;
import org.kuga.kip.datastore.DatastoreRepository;
import org.kuga.kip.datastore.FieldValue;
import org.kuga.kip.datastore.FieldValueRepository;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.datastore.field.FieldRepository;
import org.kuga.kip.datastore.value.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@CrossOrigin(origins = "http://localhost:49155")
@RestController
@RequestMapping("/row")
public class RowController {

    @Autowired
    DatastoreRepository datastoreRepository;

    @Autowired
    RowRepository rowRepository;

    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    FieldValueRepository fieldValueRepository;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Row> getById(@PathVariable Long id) {
        Optional<Row> jobListing = rowRepository.findById(id);
        return jobListing.map(listing -> new ResponseEntity<>(listing, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<Row>> getAll() {
        try {
            List<Row> rows = rowRepository.findAll();

            if (rows.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

            return new ResponseEntity<>(rows, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<Row> add(@RequestBody String requestData) {
        try {
            JSONObject requestObject = new JSONObject(requestData);
            Row row = new Row();
            JSONArray fieldValuesArray = requestObject.getJSONArray("fieldValues");
            addValuesToRow(fieldValuesArray, row);
            Datastore datastore = datastoreRepository.getById(requestObject.getJSONObject("datastore").getLong("id"));
            datastore.addRows(row);
            datastoreRepository.save(datastore);
            return new ResponseEntity<>(row, HttpStatus.CREATED);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update/{rowId}", consumes = "application/json")
    public ResponseEntity<Row> update(@PathVariable("rowId") Long rowId, @RequestBody String requestData) {
        try {
            JSONObject requestObject = new JSONObject(requestData);
            Optional<Row> rowOptional = rowRepository.findById(rowId);
            if (rowOptional.isPresent()) {
                Row row = rowOptional.get();
                if (requestObject.has("valuesToAdd")) {
                    addValuesToRow(requestObject.getJSONArray("valuesToAdd"), row);
                }
                if (requestObject.has("valuesToUpdate")) {
                    JSONObject valuesToUpdateObject = requestObject.getJSONObject("valuesToUpdate");
                    for (String fieldValueIdKey : valuesToUpdateObject.keySet()) {
                        Optional<FieldValue> fieldValueOptional = fieldValueRepository.findById(Long.valueOf(fieldValueIdKey));
                        if (fieldValueOptional.isPresent()) {
                            FieldValue fieldValue = fieldValueOptional.get();
                            Value<?> value = Value.build(valuesToUpdateObject.getJSONObject(fieldValueIdKey), fieldValue.getField().getType());
                            fieldValue.setValue(value);
                            fieldValueRepository.save(fieldValue);
                        }
                    }
                }
                if (requestObject.has("valuesToDelete")) {
                    JSONArray valuesToDeleteArray = requestObject.getJSONArray("valuesToDelete");
                    for (Object fieldValueIdObject : valuesToDeleteArray) {
                        Long fieldValueId = Long.valueOf(fieldValueIdObject.toString());
                        Optional<FieldValue> fieldValueOptional = fieldValueRepository.findById(fieldValueId);
                        if (fieldValueOptional.isPresent()) {
                            FieldValue fieldValue = fieldValueOptional.get();
                            row.deleteFieldValues(fieldValue);
                            rowRepository.save(row);
                            Field field = fieldValue.getField();
                            field.deleteFieldValues(fieldValue);
                            fieldRepository.save(field);
                            fieldValueRepository.deleteById(fieldValueId);
                        }
                    }
                }
                Row _row = rowRepository.save(row);
                return new ResponseEntity<>(_row, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void addValuesToRow(JSONArray fieldValuesArray, Row row) throws ParseException, MalformedURLException {
        for (Object o : fieldValuesArray) {
            JSONObject fieldValuesObject = (JSONObject) o;
            Optional<Field> fieldOptional = fieldRepository.findById(fieldValuesObject.getJSONObject("field").getLong("id"));
            if (fieldOptional.isPresent()) {
                Field field = fieldOptional.get();
                Value<?> value = Value.build(fieldValuesObject.getJSONObject("value"), field.getType());
                FieldValue fieldValue = new FieldValue(field, value);
                row.addFieldValues(fieldValue);
            }
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        try {
            Optional<Row> rowOptional = rowRepository.findById(id);
            Optional<Datastore> datastoreOptional;
            if (rowOptional.isPresent()) {
                Row row = rowOptional.get();
                datastoreOptional = datastoreRepository.findById(row.getDatastore().getId());
                DatastoreController.deleteFieldValues(row, fieldRepository, rowRepository, fieldValueRepository);
                Datastore datastore = row.getDatastore();
                datastore.deleteRows(row);
                rowRepository.delete(row);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (datastoreOptional.isPresent()) {
                Datastore datastore = datastoreOptional.get();
                datastoreRepository.save(datastore);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
