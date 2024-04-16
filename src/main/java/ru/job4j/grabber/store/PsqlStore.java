package ru.job4j.grabber.store;

import ru.job4j.grabber.parse.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver_class"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "INSERT INTO post(id, name, text, link, created) "
                                     + "VALUES(?, ?, ?, ?, ?) "
                                     + "ON CONFLICT (link) DO NOTHING")) {
            statement.setInt(1, post.getId());
            statement.setString(2, post.getTitle());
            statement.setString(3, post.getDescription());
            statement.setString(4, post.getLink());
            statement.setTimestamp(5, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM post"
        )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                postList.add(postResult(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM post WHERE id= ?"
        )) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                post = postResult(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post postResult(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String title = resultSet.getString(2);
        String description = resultSet.getString(3);
        String link = resultSet.getString(4);
        LocalDateTime created = resultSet.getTimestamp(5).toLocalDateTime();
        Post post = new Post(id, title, description, link, created);
        post.setId(id);
        post.setTitle(title);
        post.setDescription(description);
        post.setLink(link);
        post.setCreated(created);
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        try (InputStream input = PsqlStore.class.getClassLoader()
                .getResourceAsStream("post.properties")) {
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore store = new PsqlStore(config);
        Post post1 = new Post(1, "java", "desc1", "http://one.ru", LocalDateTime.now());
        Post post2 = new Post(2, "java+", "desc2", "http://two.ru", LocalDateTime.now());
        store.save(post1);
        store.save(post2);

        List<Post> post = store.getAll();
        post.forEach(System.out::println);

        System.out.println(store.findById(2));
    }
}