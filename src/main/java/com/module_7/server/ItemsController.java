package com.module_7.server;

import com.module_7.server.ItemsEntity;
import com.module_7.server.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;

    @PostMapping
    public ResponseEntity<ItemsEntity> addItem(@RequestBody ItemsEntity item) {
        ItemsEntity savedItem = itemsService.addItem(item.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    // WARNING: This endpoint is for educational purposes and demonstrates a security vulnerability
    @PostMapping("/unsafe")
    public ResponseEntity<ItemsEntity> addItemUnsafe(@RequestBody ItemsEntity item) {
        ItemsEntity savedItem = itemsService.addItemUnsafe(item.getDescription());
        // The status CREATED is sent back, but you might not have the generated ID since this is a simulated unsafe operation
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        try {
            itemsService.deleteItem(id);
            // Return a custom message with the ID of the deleted item
            return ResponseEntity.ok("Deleted item " + id + " successfully");
        } catch (EmptyResultDataAccessException e) {
            // Return a message indicating that the item was not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with ID " + id + " not found");
        }
    }


    @GetMapping
    public List<ItemsEntity> getAllItems() {
        return itemsService.getAllItems();
    }
}