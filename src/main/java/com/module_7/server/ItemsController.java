package com.module_7.server;

import com.module_7.server.ItemsEntity;
import com.module_7.server.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;

    @PostMapping
    public ItemsEntity addItem(@RequestBody ItemsEntity item) {
        itemsService.addItem(item.getDescription());
        return item;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            itemsService.deleteItem(id);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<ItemsEntity> getAllItems() {
        return itemsService.getAllItems();
    }
}
