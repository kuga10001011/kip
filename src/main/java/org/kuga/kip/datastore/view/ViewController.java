package org.kuga.kip.datastore.view;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.datastore.Datastore;
import org.kuga.kip.datastore.DatastoreRepository;
import org.kuga.kip.datastore.field.Field;
import org.kuga.kip.datastore.field.FieldRepository;
import org.kuga.kip.user.User;
import org.kuga.kip.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@CrossOrigin(origins = "http://localhost:49155")
@RestController
@RequestMapping("/view")
public class ViewController {

    @Autowired
    ViewRepository viewRepository;

    @Autowired
    DatastoreRepository datastoreRepository;

    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<View> add(@RequestParam Long datastoreId, @RequestParam String name, @RequestBody String requestString) {
        try {
            Optional<Datastore> datastoreOptional = datastoreRepository.findById(datastoreId);
            if (datastoreOptional.isPresent()) {
                Datastore datastore = datastoreOptional.get();
                View view = new View();
                view.setName(name);
                view.setDatastore(datastore);
                JSONObject requestObject = new JSONObject(requestString);
                if (requestObject.has("fields")) {
                    JSONArray fieldIdsArray = requestObject.getJSONArray("fields");
                    for (Object fieldIdObject : fieldIdsArray) {
                        Optional<Field> fieldOptional = fieldRepository.findById(Long.valueOf(fieldIdObject.toString()));
                        fieldOptional.ifPresent(view::addFields);
                    }
                }
                else {
                    throw new RuntimeException("no fields provided for view");
                }
                View _view = viewRepository.save(view);
                return new ResponseEntity<>(_view, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/", params = {"datastoreId, userId"})
    public ResponseEntity<List<View>> getAllForDatastoreAndUser(@RequestParam Long datastoreId, @RequestParam Long userId) {
        try {
            Optional<Datastore> datastoreOptional = datastoreRepository.findById(datastoreId);
            Optional<User> userOptional = userRepository.findById(userId);
            if (datastoreOptional.isPresent() && userOptional.isPresent()) {
                Optional<List<View>> views = viewRepository.findAllByDatastoreAndUsersContaining(datastoreOptional.get(), userOptional.get());
                return views.map(viewList -> new ResponseEntity<>(viewList, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));
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
