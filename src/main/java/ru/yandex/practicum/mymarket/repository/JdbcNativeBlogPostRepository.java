package ru.yandex.practicum.mymarket.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.Paging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcNativeBlogPostRepository implements MarketRepository, CartRepository, OrderRepository, FileRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JdbcNativeBlogPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        String baseSql = "SELECT id, title, description, img_path, price, count FROM items WHERE 1=1";
        StringBuilder sqlBuilder = new StringBuilder(baseSql);

        if (search != null && !search.isBlank()) {
            sqlBuilder.append(" AND (title LIKE ? OR description LIKE ?)");
        }

        if (sort != null) {
            switch (sort) {
                case "ALPHA":
                    sqlBuilder.append(" ORDER BY title ASC");
                    break;
                case "PRICE":
                    sqlBuilder.append(" ORDER BY price ASC");
                    break;
                default:
                    sqlBuilder.append(" ORDER BY id ASC");
            }
        } else {
            sqlBuilder.append(" ORDER BY id ASC");
        }

        sqlBuilder.append(" LIMIT ? OFFSET ?");

        List<Object> params = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            String likePattern = "%" + search + "%";
            params.add(likePattern);
            params.add(likePattern);
        }
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(sqlBuilder.toString(), params.toArray(), (rs, rowNum) -> new Item(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("img_path"),
                rs.getLong("price"),
                rs.getInt("count")
        ));
    }

    @Override
    public Paging getPaging(String search, int pageNumber, int pageSize) {

        String countSql = "SELECT COUNT(*) FROM items WHERE 1=1";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            countSql += " AND (title LIKE ? OR description LIKE ?)";
            String likePattern = "%" + search + "%";
            params.add(likePattern);
            params.add(likePattern);
        }

        int total = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);

        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasNext = pageNumber < totalPages;
        boolean hasPrev = pageNumber > 1;

        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        paging.setHasPrev(hasPrev);
        paging.setHasNext(hasNext);
        return paging;
    }

    @Override
    public Item getItem(Long id) {
        String sql = "SELECT id, title, description, img_path, price, count FROM items WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Item(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("img_path"),
                rs.getLong("price"),
                rs.getInt("count")
        ));
    }

    @Transactional
    @Override
    public Item getIncreaseOrDecreaseItem(Long id, String action) {
        String sql = "PLUS".equalsIgnoreCase(action)
                ? "UPDATE items SET count = count + 1 WHERE id = ?"
                : "UPDATE items SET count = count - 1 WHERE id = ? AND count > 0";
        jdbcTemplate.update(sql, id);
        return getItem(id);
    }

    @Override
    public List<Item> getCartItems() {
        String sql = "SELECT id, title, description, img_path, price, count FROM items WHERE count > 0";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Item(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("img_path"),
                rs.getLong("price"),
                rs.getInt("count")
        ));
    }

    @Override
    public int getTotal() {
        String sql = "SELECT COALESCE(SUM(price * count), 0) FROM items WHERE count > 0";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Transactional
    @Override
    public List<Item> getIncreaseOrDecreaseCartItem(Long id, String action) {
        String updateSql = switch (action.toUpperCase()) {
            case "PLUS" -> "UPDATE items SET count = count + 1 WHERE id = ?";
            case "MINUS" -> "UPDATE items SET count = count - 1 WHERE id = ? AND count > 0";
            case "DELETE" -> "UPDATE items SET count = 0 WHERE id = ?";
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
        jdbcTemplate.update(updateSql, id);

        return getCartItems();
    }

    @Override
    public void addImage(int id, byte[] imageData, String contentType) {
        String sql = "UPDATE items SET image_data = ? WHERE id = ?";
        jdbcTemplate.update(sql, imageData, id);
    }

    @Override
    public List<Order> getOrders() {
        String ordersSql = "SELECT id, total_sum, created_at FROM orders ORDER BY created_at DESC";
        List<Order> orders = jdbcTemplate.query(ordersSql, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setTotalSum(rs.getLong("total_sum"));
            return order;
        });

        String itemsSql = """
        SELECT i.id, i.title, i.price, oi.count
        FROM order_items oi
        JOIN items i ON oi.item_id = i.id
        WHERE oi.order_id = ?
        """;

        for (Order order : orders) {
            List<Item> items = jdbcTemplate.query(itemsSql, new Object[]{order.getId()}, (rs, rowNum) -> {
                Item dto = new Item();
                dto.setId(rs.getLong("id"));
                dto.setTitle(rs.getString("title"));
                dto.setPrice(rs.getLong("price"));
                dto.setCount(rs.getInt("count"));
                return dto;
            });
            order.setItems(items);
        }
        return orders;
    }

    @Override
    public Order getNewOrder(Long id) {
        String orderSql = "SELECT id, total_sum, created_at FROM orders WHERE id = ?";
        Order order = jdbcTemplate.queryForObject(orderSql, new Object[]{id}, (rs, rowNum) -> {
            Order o = new Order();
            o.setId(rs.getLong("id"));
            o.setTotalSum(rs.getLong("total_sum"));
            return o;
        });

        if (order == null) return null;

        String itemsSql = """
        SELECT i.id, i.title, i.price, oi.count
        FROM order_items oi
        JOIN items i ON oi.item_id = i.id
        WHERE oi.order_id = ?
        """;
        List<Item> items = jdbcTemplate.query(itemsSql, new Object[]{id}, (rs, rowNum) -> {
            Item dto = new Item();
            dto.setId(rs.getLong("id"));
            dto.setTitle(rs.getString("title"));
            dto.setPrice(rs.getLong("price"));
            dto.setCount(rs.getInt("count"));
            return dto;
        });
        order.setItems(items);
        return order;
    }

    @Transactional
    @Override
    public Long createOrderFromCart() {
        String selectCartSql = "SELECT id, price, count FROM items WHERE count > 0";
        List<Item> cartItems = jdbcTemplate.query(selectCartSql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setPrice(rs.getLong("price"));
            item.setCount(rs.getInt("count"));
            return item;
        });

        long totalSum = cartItems.stream()
                .mapToLong(item -> item.getPrice() * item.getCount())
                .sum();

        SimpleJdbcInsert insertOrder = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("orders")
                .usingGeneratedKeyColumns("id");

        long orderId = insertOrder.executeAndReturnKey(Map.of("total_sum", totalSum, "created_at", new Date())).longValue();

        String insertOrderItemSql = "INSERT INTO order_items (order_id, item_id, count) VALUES (?, ?, ?)";
        for (Item item : cartItems) {
            jdbcTemplate.update(insertOrderItemSql, orderId, item.getId(), item.getCount());
        }

        String clearCartSql = "UPDATE items SET count = 0 WHERE count > 0";
        jdbcTemplate.update(clearCartSql);

        return orderId;
    }
}
