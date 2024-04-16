package ru.job4j.grabber.store;

import ru.job4j.grabber.parse.Post;

import java.util.List;

public interface Store extends AutoCloseable {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}
