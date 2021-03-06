package com.desafio.achadoseperdidos.services.interfaces;

import com.desafio.achadoseperdidos.dto.ItemDTO;
import com.desafio.achadoseperdidos.entities.Item;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ItemService {
    Item createItem(Item item);

    List<Item> getAllItems(Map<String, String> fieldToFilter);

    Item updateItem(UUID itemId, ItemDTO itemDTO);
}
