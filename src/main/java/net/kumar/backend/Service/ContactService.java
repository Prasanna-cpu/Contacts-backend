package net.kumar.backend.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kumar.backend.Repository.ContactRepository;
import net.kumar.backend.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.kumar.backend.Utils.Constant.Constant.PHOTO_DIR;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public Page<Contact> getAllContact(int page,int size){
        return contactRepository.findAll(PageRequest.of(page, size,Sort.by("name")));
    }

    public Contact getContactById(String id){
        return contactRepository.findById(id).orElseThrow(()->new RuntimeException("Contact not found"));
    }

   public Contact createContact(Contact contact){
        return contactRepository.save(contact);
   }
   public void deleteContact(String id){
        Contact targetContact=contactRepository.findById(id).orElseThrow(()->new RuntimeException("Contact not found"));
        contactRepository.delete(targetContact);
   }
   public Contact updateContactById(Contact setterContact,String id){
        Contact targetContact=contactRepository.findById(id).orElseThrow(()->new RuntimeException("Contact not found"));
        targetContact.setName(setterContact.getName());
        targetContact.setEmail(setterContact.getEmail());
        targetContact.setPhone(setterContact.getPhone());
        targetContact.setAddress(setterContact.getAddress());
        targetContact.setStatus(setterContact.getStatus());
        return contactRepository.save(targetContact);
   }


   public String uploadPhoto(String id, MultipartFile file){
       log.info("uploading photo{}", file.getOriginalFilename());
        Contact contact=getContactById(id);
        String photoUrl=photoUploaderFunction.apply(id,file);
        contact.setUrls(photoUrl);
        contactRepository.save(contact);
        return photoUrl;

   }

   private final Function<String,String> fileExtension= filename-> Optional.of(filename)
           .filter(name->name.contains("."))
           .map(name->'.'+name.substring(filename.lastIndexOf(".")+1)).orElse(".png");


   private final BiFunction<String,MultipartFile,String>  photoUploaderFunction =(id,image)-> {
       String filename=id+fileExtension.apply(image.getOriginalFilename());

       try {
           Path fileStorePath = Paths.get(PHOTO_DIR).toAbsolutePath().normalize();
           if (!Files.exists(fileStorePath)) {
               Files.createDirectories(fileStorePath);
           }
           Files.copy(image.getInputStream(), fileStorePath.resolve(id + fileExtension.apply(image.getOriginalFilename())), REPLACE_EXISTING);
           return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contacts/image/" +filename).toUriString();
       } catch (Exception e) {

           log.error("Error in uploading file:{}",e.getMessage());
       }
       return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contacts/image/" +filename).toUriString();
   };








}
