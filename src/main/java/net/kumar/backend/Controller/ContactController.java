package net.kumar.backend.Controller;

import lombok.RequiredArgsConstructor;
import net.kumar.backend.Service.ContactService;
import net.kumar.backend.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.kumar.backend.Utils.Constant.Constant.PHOTO_DIR;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping()
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact){
        return ResponseEntity.created(URI.create("/contact/contactId")).body(contactService.createContact(contact));
    }

    @GetMapping()
    public ResponseEntity<Page<Contact>> getAllContacts(@RequestParam(value = "page",defaultValue = "0") int page,@RequestParam(value = "size",defaultValue = "10") int size){
        return ResponseEntity.ok().body(contactService.getAllContact(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable(value = "id") String id){
        return ResponseEntity.ok().body(contactService.getContactById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContactById(@PathVariable(value = "id") String id,@RequestBody Contact contact){
        return ResponseEntity.accepted().body(contactService.updateContactById(contact,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable(value = "id") String id){
        contactService.deleteContact(id);
        return ResponseEntity.ok().body("Contact deleted successfully");
    }



    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id,@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok().body(contactService.uploadPhoto(id,file));
    }

    @GetMapping(path = "/image/{filename}",produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE,IMAGE_GIF_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIR + filename));
    }



}
