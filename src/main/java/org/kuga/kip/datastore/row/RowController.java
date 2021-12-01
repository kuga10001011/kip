package org.kuga.kip.datastore.row;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.datastore.Datastore;
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

import java.util.Collections;
import java.util.Iterator;
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

    @GetMapping(value = "/", params = {"id"})
    public ResponseEntity<Row> getById(@RequestParam Long id) {
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
            Datastore datastore = datastoreRepository.getById(requestObject.getJSONObject("datastore").getLong("id"));
            datastore.addRows(row);
            datastoreRepository.save(datastore);
            return new ResponseEntity<>(row, HttpStatus.CREATED);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        try {
            Row row = rowRepository.findById(id).get();
            for (FieldValue fieldValue: row.getFieldValues()) {
                Field field = fieldValue.getField();
                field.removeFieldValues(fieldValue);
                fieldRepository.save(field);
                fieldValueRepository.deleteById(fieldValue.getId());
            }
            Datastore datastore = row.getDatastore();
            datastore.deleteRows(row);
            datastoreRepository.save(datastore);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
