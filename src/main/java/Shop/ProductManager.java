package Shop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/shop_product?" +
            "allowPublicKeyRetieval=true&useSSL=false&useUnicode=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Product> getProducts() {
        final String sqlSelectProduct = "SELECT * FROM product";
        List<Product> products = new ArrayList<Product>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlSelectProduct)) {
            while (resultSet.next()) {
                Product product = new Product(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("price"));
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date_created"), fmt);
                product.setDateCreated(dateTime);
                products.add(product);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return products;
    }

    public void editProduct(Product product) {
        final String sqlProductUpdate = "UPDATE product SET name=?, price=?, date_created=? WHERE id=?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlProductUpdate)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getPrice());
            LocalDateTime dateTime = LocalDateTime.parse(product.getDateCreated().toString());
            String dateAsString = dateTime.format(fmt);
            statement.setString(3, dateAsString);
            statement.setInt(4, product.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addProduct(Product product) {
        final String sqlProductAdd = "INSERT INTO product (id, name, price, date_created) VALUES (?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlProductAdd)) {
            statement.setInt(1, product.getId());
            statement.setString(2, product.getName());
            statement.setInt(3, product.getPrice());
            LocalDateTime dateTime = LocalDateTime.parse(product.getDateCreated().toString());
            String dateAsString = dateTime.format(fmt);
            statement.setString(4, dateAsString);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int removeProduct(int id) {
        final String sqlProductRemove = "DELETE FROM product WHERE id=?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlProductRemove)) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public Product findProductById(int id) {
        final String sqlFindProduct = "Select * from product where id=?;";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlFindProduct)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Product(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("price"));
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public void createPurchaseProduct(Product product, int purchaseId) {
        final String sqlCopyProduct = "INSERT INTO purchase_product (purchase_id, name, price) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlCopyProduct)) {
            statement.setInt(1, purchaseId);
            statement.setString(2, product.getName());
            statement.setInt(3, product.getPrice());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getPurchaseHistory() {
        final String sqlSelectPurchase = "SELECT * FROM purchase";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlSelectPurchase)) {
            while (resultSet.next()) {
                System.out.printf("Id: %d   Purchase date: %s\n", resultSet.getInt("id"), resultSet.getString("purchase_date"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void getProductPurchaseHistory(int purchaseId) {
        final String sqlSelectProductHistory = "SELECT pp.name,pp.price\n" +
                "FROM  purchase_product pp\n" +
                "WHERE pp.purchase_id=?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlSelectProductHistory)) {
            statement.setInt(1, purchaseId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.printf("Product name: %s   Product price: %d\n", resultSet.getString("name"), resultSet.getInt("price"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int createPurchase() {
        final String sqlPurchaseDate = "INSERT INTO purchase (purchase_date) VALUE (?)";
        LocalDateTime purchaseDateNow = LocalDateTime.now();
        LocalDateTime purchaseDate = LocalDateTime.parse(purchaseDateNow.toString());
        String dateAsString = purchaseDate.format(fmt);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sqlPurchaseDate)) {
            statement.setString(1, dateAsString);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        final String sqlGetPurchaseId = "SELECT max(id) as id FROM purchase";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlGetPurchaseId)){
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }else{
                return 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }
}

