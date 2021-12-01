package org.kuga.kip.datastore.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@CrossOrigin(origins = "http://localhost:49155")
@RestController
@RequestMapping("/field")
public class FieldController {

    @Autowired
    FieldRepository fieldRepository;

    @GetMapping(value = "/types")
    public ResponseEntity<Type[]> getTypes() {
        try {
            return new ResponseEntity<>(Type.values(), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
