package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "0") int from,   // Начальный индекс
            @RequestParam(defaultValue = "10") int size,  // Количество постов на странице
            @RequestParam(defaultValue = "desc") String sort // Порядок сортировки (asc или desc)
    ) {
        // Преобразуем строку параметра sort в перечисление SortOrder
        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + ", должно быть: asc или desc");
        }

        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }

        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }

        // Возвращаем результат вызова метода сервиса с параметрами
        return postService.findAll(from, size, sortOrder);
    }

    @GetMapping("/{id}")
    public Post findPostById(@PathVariable Long id) {
        return postService.findById(id)
                .orElseThrow(() -> new NotFoundException("Пост с id = " + id + " не найден"));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}