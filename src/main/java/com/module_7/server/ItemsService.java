package com.module_7.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ItemsService {

    private final ItemsRepository itemsRepository;

    @Autowired
    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Transactional
    public ItemsEntity addItem(String description) {
        ItemsEntity item = new ItemsEntity();
        item.setDescription(description);
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return itemsRepository.save(item); // Saving the item and returning the persisted entity
    }

    @Transactional(readOnly = true)
    public List<ItemsEntity> getAllItems() {
        return itemsRepository.findAll(); // Finding all items
    }
}
