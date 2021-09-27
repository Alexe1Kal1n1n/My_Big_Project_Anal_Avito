package com.amr.project.webapp.rest_controller;

import com.amr.project.converter.ItemMapper;
import com.amr.project.model.dto.ItemDto;
import com.amr.project.model.entity.Item;
import com.amr.project.service.impl.CategoryServiceImpl;
import com.amr.project.service.impl.ItemServiceImpl;
import com.amr.project.service.impl.ShopServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Validated
@RestControllerAdvice
@RestController
@RequiredArgsConstructor
@RequestMapping("shop/")
public class ItemRestController {

    private final CategoryServiceImpl categoryService;

    private final ShopServiceImpl shopService;

    private final ItemServiceImpl itemServiceImpl;

    private final ItemMapper itemConverter;

    @PostMapping("item")
    public ResponseEntity<?> addItem(@RequestBody ItemDto itemDto) {
        Item item = itemConverter.dtoToItem(itemDto);
        if (itemDto.getShopName() == null || itemDto.getCategoriesName() == null)
            return ResponseEntity.badRequest().body("empty shop");
        item.setCategories(Arrays.stream(itemDto.getCategoriesName()).map(category -> categoryService.getCategory(category)).collect(Collectors.toList()));
        item.setShop(shopService.getShop(itemDto.getShopName()));
        itemServiceImpl.persist(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("item/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable @NonNull Long id) {
        Item item = itemServiceImpl.getByKey(id);
        if (item == null)
            return ResponseEntity.badRequest().body("empty item");
        item.setPretendentToBeDeleted(true);
        itemServiceImpl.update(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping("item")
    public ResponseEntity<ItemDto> updateItem(@RequestBody @NonNull ItemDto itemDto) {
        Item item = itemConverter.dtoToItem(itemDto);
        if (itemDto.getId() == null || itemDto.getShopName() == null || itemDto.getCategoriesName() == null)
            return ResponseEntity.badRequest().build();
        item.setId(itemDto.getId());
        item.setShop(shopService.getShop(itemDto.getShopName()));
        item.setCategories(Arrays.stream(itemDto.getCategoriesName())
                .map(category -> categoryService.getCategory(category))
                .collect(Collectors.toList()));
        itemServiceImpl.update(item);
        return ResponseEntity.ok().body(itemConverter.itemToDto(item));
    }
}

