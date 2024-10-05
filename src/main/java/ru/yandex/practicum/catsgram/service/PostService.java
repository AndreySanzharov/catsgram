package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    // Метод для получения списка постов с поддержкой сортировки и пагинации
    public List<Post> findAll(int from, int size, SortOrder sortOrder) {
        // Сортировка по дате создания (по возрастанию или убыванию)
        Comparator<Post> comparator = Comparator.comparing(Post::getPostDate);
        if (sortOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        // Применяем сортировку и возвращаем посты с учетом пагинации
        return posts.values().stream()
                .sorted(comparator)
                .skip(from) // Пропускаем указанное количество постов
                .limit(size) // Возвращаем указанное количество постов
                .collect(Collectors.toList());
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
            post.setId(getNextId());
            post.setPostDate(Instant.now());
            posts.put(post.getId(), post);
            return post;
    }

    public Optional<Post> findPostById(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}