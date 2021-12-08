package org.kuga.kip.datastore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.datastore.field.FieldRepository;
import org.kuga.kip.datastore.row.Row;
import org.kuga.kip.datastore.row.RowRepository;
import org.kuga.kip.datastore.value.ValueRepository;
import org.kuga.kip.datastore.view.ViewRepository;
import org.kuga.kip.user.User;
import org.kuga.kip.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Transactional
@CrossOrigin(origins = "http://localhost:49155")
@RestController
@RequestMapping("/datastore")
public class DatastoreController {

    @Autowired
    DatastoreRepository datastoreRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    RowRepository rowRepository;

    @Autowired
    FieldValueRepository fieldValueRepository;

    @Autowired
    ViewRepository viewRepository;

    @Autowired
    ValueRepository valueRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/")
    public ResponseEntity<List<Datastore>> getAll() {
        try {
            List<Datastore> datastores = datastoreRepository.findAll();

            if (datastores.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

            return new ResponseEntity<>(datastores, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/", params = {"datastoreId"})
    public ResponseEntity<Datastore> getById(@RequestParam Long datastoreId) {
        try {
            Optional<Datastore> datastore = datastoreRepository.findById(datastoreId);

            return datastore.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/", params = {"userId"})
    public ResponseEntity<List<Datastore>> getAllForUser(@RequestParam Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                List<Datastore> datastores = datastoreRepository.findDistinctByUsersContaining(user.get());
                return new ResponseEntity<>(datastores, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        try {
            Optional<Datastore> datastoreOptional = datastoreRepository.findById(id);
            if (datastoreOptional.isPresent()) {
                Datastore datastore = datastoreOptional.get();
                for (User user : datastore.getUsers()) {
                    user.deleteDatastore(datastore);
                    userRepository.save(user);
                }
                Iterator<Row> rowIterator = datastore.getRows().iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    deleteFieldValues(row, fieldRepository, rowRepository, fieldValueRepository);
                    rowIterator.remove();
                    rowRepository.delete(row);
                }
                datastoreRepository.save(datastore);
                Iterator<Field> fieldIterator = datastore.getFields().iterator();
                while (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    fieldIterator.remove();
                    fieldRepository.delete(field);
                }
                datastoreRepository.save(datastore);
                datastoreRepository.deleteById(id);
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

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<Datastore> add(@RequestBody String requestData) {
        try {
            JSONObject requestObject = new JSONObject(requestData);
            Datastore datastore = new Datastore();
            datastore.setName(requestObject.getString("name"));
            if (requestObject.has("fields")) {
                JSONArray fieldObjects = requestObject.getJSONArray("fields");
                for (Object object : fieldObjects) {
                    JSONObject fieldObject = (JSONObject) object;
                    Field field = new Field(fieldObject);
                    datastore.addFields(field);
                }
            }
            Datastore _datastore = saveDatastore(requestObject, datastore);
            return new ResponseEntity<>(_datastore, HttpStatus.CREATED);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Datastore saveDatastore(JSONObject requestObject, Datastore datastore) {
        Optional<User> user = userRepository.findById(requestObject.getLong("userId"));
        if (user.isPresent()) {
            datastore.addUsers(user.get());
        }
        else {
            throw new RuntimeException(String.format("user not found: %d", requestObject.getLong("userId")));
        }

        return datastoreRepository.save(datastore);
    }

    @PutMapping(value = "/update/", params="datastoreId", consumes = "application/json")
    public ResponseEntity<Datastore> update(@RequestParam("datastoreId") Long datastoreId, @RequestBody String requestData) {
        try {
            JSONObject requestObject = new JSONObject(requestData);
            Optional<Datastore> datastoreData = datastoreRepository.findById(datastoreId);
            if (datastoreData.isPresent()) {
                Datastore datastore = datastoreData.get();
                datastore.setName(requestObject.getString("name"));
                if (requestObject.has("fieldsToAdd")) {
                    JSONArray fieldObjects = requestObject.getJSONArray("fieldsToAdd");
                    for (Object object : fieldObjects) {
                        JSONObject fieldObject = (JSONObject) object;
                        Field field = new Field(fieldObject);
                        datastore.addFields(field);
                    }
                }
                if (requestObject.has("fieldsToDelete")) {
                    JSONArray fieldObjects = requestObject.getJSONArray("fieldsToDelete");
                    for (Object object: fieldObjects) {
                        Long fieldId = Long.valueOf(object.toString());
                        Optional<Field> fieldOptional = fieldRepository.findById(fieldId);
                        if (fieldOptional.isPresent()) {
                            Field field = fieldOptional.get();
                            datastore.deleteFields(field);
                            fieldRepository.delete(field);
                        }
                    }
                }
                if (requestObject.has("fieldsToUpdate")) {
                    JSONObject fieldObjects = requestObject.getJSONObject("fieldsToUpdate");
                    for (String fieldId : fieldObjects.keySet()) {
                        JSONObject fieldObject = fieldObjects.getJSONObject(fieldId);
                        Optional<Field> fieldOptional = fieldRepository.findById(fieldObject.getLong("id"));
                        if (fieldOptional.isPresent()) {
                            Field field = fieldOptional.get();
                            field.setField(fieldObject);
                            fieldRepository.save(field);
                        }
                    }
                }
                Datastore _datastore = saveDatastore(requestObject, datastore);
                return new ResponseEntity<>(_datastore, HttpStatus.ACCEPTED);
            }
            else {
                throw new RuntimeException(String.format("datastore not found: %d", requestObject.getLong("datastoreId")));
            }
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Datastore> get(@PathVariable("id") Long id) {
        Optional<Datastore> datastoreData = datastoreRepository.findById(id);
        return datastoreData.map(datastore -> new ResponseEntity<>(datastore, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public static void deleteFieldValues(Row row, FieldRepository fieldRepository, RowRepository rowRepository, FieldValueRepository fieldValueRepository) {
        for (FieldValue fieldValue: row.getFieldValues()) {
            Field field = fieldValue.getField();
            field.deleteFieldValues(fieldValue);
            fieldRepository.save(field);
            row.deleteFieldValues(fieldValue);
            rowRepository.save(row);
            fieldValueRepository.delete(fieldValue);
        }
    }

}
