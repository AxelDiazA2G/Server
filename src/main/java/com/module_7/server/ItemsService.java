package com.module_7.server;

import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ItemsService {
    private static final Logger logger = LoggerFactory.getLogger(ItemsService.class);

    private final ItemsRepository itemsRepository;
    private final EntityManager entityManager; // Add this line
    @Autowired
    public ItemsService(ItemsRepository itemsRepository, EntityManager entityManager) {
        this.itemsRepository = itemsRepository;
        this.entityManager = entityManager; // Initialize EntityManager
    }

    @Transactional
    public ItemsEntity addItem(String description) {
        ItemsEntity item = new ItemsEntity();
        item.setDescription(description);
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return itemsRepository.save(item); // Saving the item and returning the persisted entity
    }

    @Transactional
    public ItemsEntity addItemUnsafe(String description) {
        // SQL Injection command is part of the 'description' parameter
        String sqlInsert = "INSERT INTO items (description, created_at) VALUES ('" + description + "', CURRENT_TIMESTAMP)";
        logger.warn("Executing unsafe insert SQL: {}", sqlInsert);
        entityManager.createNativeQuery(sqlInsert).executeUpdate();

        // This select might not work as expected after DELETE operation, it's just for the example
        String sqlSelect = "SELECT * FROM items WHERE description = 'test' ORDER BY created_at DESC LIMIT 1";
        logger.warn("Executing unsafe select SQL: {}", sqlSelect);
        ItemsEntity item = null;
        try {
            item = (ItemsEntity) entityManager.createNativeQuery(sqlSelect, ItemsEntity.class).getSingleResult();
        } catch (NoResultException e) {
            logger.error("No items found after injection: {}", e.getMessage());
        }

        return item; // Return the item retrieved from the database or null
    }



    @Transactional
    public void deleteItem(Long id) {
        itemsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ItemsEntity> getAllItems() {
        try {
            return itemsRepository.findAll(); // Finding all items
        } catch (Exception e) {
            // Log the exception details here
            System.err.println("An error occurred while fetching all items: " + e.getMessage());
            throw e;
        }
    }

}
