package Shop;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shop {
    private final Scanner scanner = new Scanner(System.in);
    private final String path = "XMLTest/src/main/java/Shop/ProductList.json";
    private final ProductGenerator productGenerator;
    private final ProductManager productManager = new ProductManager();

    public Shop() {
        int maxId = productManager.getProducts().stream()
                .max(Comparator.comparingInt(product -> product.getId()))
                .map(product -> product.getId())
                .orElse(0);
        productGenerator = new ProductGenerator(maxId);
    }

    public void start() {
        boolean isStart = true;
        while (isStart) {
            int action = getMainPage();
            if (action == 1) {
                getDisplayAndSortPage();
            } else if (action == 2) {
                getAddProductPage();
            } else if (action == 3) {
                getRemoveProductPage();
            } else if (action == 4) {
                displayProductList();
                editProduct();
            } else if (action == 5) {
                getProduceConsumePage();
            } else if (action == 6) {
                getBuyProductsPage();
            } else if (action == 7) {
                System.out.println("Выход из программы");
                scanner.close();
                isStart = false;
            }
        }
    }

    private void addProduct(Product product) {
        List<Product> products = productManager.getProducts();
        if (!products.contains(product)) {
            products.add(product);
            productManager.addProduct(product);
        }
    }

    private boolean removeProduct(int id) {
        if (productManager.removeProduct(id) == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void editProduct() {
        System.out.println("Введите ID товара для редактирования\n");
        int id = readInt();
        List<Product> products = productManager.getProducts();
        Optional<Product> productId = products.stream()
                .filter(product -> product.getId() == id)
                .findAny();
        productId.ifPresentOrElse(
                product -> {
                    product.setName(enterName());
                    product.setPrice(enterPrice());
                    productManager.editProduct(product);
                },
                () -> System.out.println("Товара с таким ID не найдено\n"));
        productId.ifPresent(product -> System.out.println(product + "\n"));
    }

    private int readInt() {
        boolean isInt = false;
        int a = 0;
        do {
            try {
                a = scanner.nextInt();
                while (a < 0) {
                    System.out.println("Введите целую положительную цифру");
                    a = scanner.nextInt();
                }
                isInt = true;
            } catch (Exception e) {
                System.out.println("Введите целую цифру\n");
                scanner.nextLine();
            }
        } while (!isInt);
        return a;
    }

    private int getMainPage() {
        String description = "\nВыберите действие:\n  1) Вывод всех товаров\n  2) Добавление товара\n  " +
                "3) Удаление товара\n  4) Редактирование товара\n  5) Имитация производства и покупки товаров\n  " +
                "6) Покупка товаров и История покупок\n  7) Выход\n";
        System.out.println(description);
        int action = readInt();
        while (action < 0 || action > 7) {
            System.out.println(description);
            action = readInt();
        }
        return action;
    }

    private void getDisplayAndSortPage() {
        int action;
        boolean isDisplayAndSortPage = true;
        while (isDisplayAndSortPage) {
            String description = "Выберите способ вывода: \n  1) По цене(возрастание):\n  2) По цене(убывание):\n" +
                    "  3) В диапазоне цены:\n  4) По добавлению:\n  5) Назад\n";
            System.out.println(description);
            action = readInt();
            while (action < 0 || action > 5) {
                System.out.println(description);
                action = readInt();
            }
            List<Product> productList = productManager.getProducts();
            if (action == 1) {
                productList.sort((Comparator<Product>) (o1, o2) -> o1.getPrice() - o2.getPrice());
            } else if (action == 2) {
                productList.sort((Comparator<Product>) (o1, o2) -> o2.getPrice() - o1.getPrice());
            } else if (action == 3) {
                System.out.println("Введите нижнюю границу цены");
                int lowerPriceLimit = readInt();
                System.out.println("Введите верхнюю границу цены");
                int upperPriceLimit = readInt();
                productList.stream()
                        .filter(product -> product.getPrice() <= upperPriceLimit)
                        .filter(product -> product.getPrice() >= lowerPriceLimit)
                        .forEach(System.out::println);
                System.out.println();
            } else if (action == 4) {
                productList.sort((Comparator<Product>) (o1, o2) -> o1.getDateCreated().compareTo(o2.getDateCreated()));
            } else if (action == 5) {
                isDisplayAndSortPage = false;
            }
            if (action != 3 && action != 5) {
                productList.forEach(product -> System.out.println(product.toString()));
                System.out.println();
            }
        }
    }

    private void displayProductList() {
        productManager.getProducts().forEach(product -> System.out.println(product.toString()));
        System.out.println();
    }

    private void getAddProductPage() {
        boolean isGetAddProductPage = true;
        String description = "1) Изменить ID\n2) Изменить наименование\n3) Изменить цену товара\n" +
                "4) Добавить в перечень товаров\n";
        Product product = new Product(enterId(), enterName(), enterPrice());
        System.out.println();
        System.out.println("Проверьте данные и добавте товар в перечень. В случае ошибки отредактируйте\n");
        System.out.println(product + "\n");
        System.out.println(description);
        int action = readInt();
        while (isGetAddProductPage) {
            if (action == 1) {
                product.setId(enterId());
                System.out.println(product + "\n");
                System.out.println(description);
                action = readInt();
            } else if (action == 2) {
                product.setName(enterName());
                System.out.println(product + "\n");
                System.out.println(description);
                action = readInt();
            } else if (action == 3) {
                product.setPrice(enterPrice());
                System.out.println(product + "\n");
                System.out.println(description);
                action = readInt();
            } else if (action == 4) {
                addProduct(product);
                System.out.println("Товар добавлен\n");
                isGetAddProductPage = false;
            } else {
                System.out.println("Ввели недопустимое значение\n");
                System.out.println(description);
                action = readInt();
            }
        }
    }

    private void getRemoveProductPage() {
        displayProductList();
        System.out.println("Введите ID товара для удаления\n");
        int id = readInt();
        if (removeProduct(id)) {
            System.out.println("Товар удален из перечня\n");
        } else {
            System.out.println("Товара с таким ID не существует\n");
        }
    }

    private int enterId() {
        System.out.println("Введите ID товара\n");
        int id = readInt();
        while (id < 1) {
            System.out.println("ID товара не должен быть меньше 1\n");
            id = readInt();
        }
        int tempId = id;
        boolean ifSameId = productManager.getProducts().stream()
                .anyMatch(product -> product.getId() == tempId);
        while (ifSameId) {
            System.out.println("Товар с таким ID уже существует либо введен неккоректно, введите другой ID\n");
            id = readInt();
            int tempId2 = id;
            boolean ifSameId2 = productManager.getProducts().stream()
                    .anyMatch(product -> product.getId() == tempId2);
            if (!ifSameId2) {
                ifSameId = false;
            }
        }
        return id;
    }

    private String enterName() {
        Pattern regExPattern = Pattern.compile("^(?U)[A-ZА-Я](\\w+)?(\\s?[\\w])+((\\s?[\\d]+)+)?");
        System.out.println("Введите наимнование товара\n");
        String productName = scanner.nextLine();
        productName = scanner.nextLine();
        Matcher matcher = regExPattern.matcher(productName);
        while (!matcher.find()) {
            System.out.println("Наименование товара введено не корректно");
            productName = scanner.nextLine();
            matcher = regExPattern.matcher(productName);
        }
        return productName;
    }

    private int enterPrice() {
        System.out.println("Введите цену товара\n");
        int price = readInt();
        while (price < 0) {
            System.out.println("Цена товара должна быть положительным числом\n");
            price = readInt();
        }
        return price;
    }

    private void getProduceConsumePage() {
        Producer producer = new Producer(this);
        Consumer consumer = new Consumer(this);
        Thread threadProducer = new Thread(producer, "thread-producer");
        Thread threadConsumer = new Thread(consumer, "thread-consumer");
        threadProducer.start();
        threadConsumer.start();
        try {
            threadProducer.join();
            threadConsumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void buyProduct() throws IOException {
        Random random = new Random();
        int randomProduct = random.nextInt(productManager.getProducts().size());
        while (productManager.getProducts().size() < 5) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (productManager) {
            List<Product> products = productManager.getProducts();
            System.out.println("[" + Thread.currentThread().getName() + "] Покупатель купил 1 \""
                    + products.get(randomProduct).getName() + "\" за " + products.get(randomProduct).getPrice());
            productManager.removeProduct(products.get(randomProduct).getId()); //Рандомный продукт добавить
        }
        notify();
    }

    public synchronized void produceProduct() throws IOException {
        while (productManager.getProducts().size() > 10) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (productManager) {
            Product product = productGenerator.generate();
            System.out.println("[" + Thread.currentThread().getName() + "] Продавец произвел товар \"" + product.getName() + "\" c ценой " + product.getPrice());
            productManager.addProduct(product);
        }
        notify();
    }

    private void getBuyProductsPage() {
        boolean isStart = true;
        String description = "Выберите действие:\n" +
                "  1)Покупка товаров\n  2)История покупок\n  3)Назад";
        while (isStart) {
            System.out.println(description);
            int action = readInt();
            if (action == 1) {
                displayProductList();
                System.out.println("\nДобавте товары в перечень покупок\n" +
                        "Введите ID товара для добавления\n" +
                        "Введите 0 для покупки добавленных товаров\n");
                int purchaseId = productManager.createPurchase();
                int id = readInt();
                while (id != 0) {
                    Product productById = productManager.findProductById(id);
                    if (productById != null) {
                        productManager.createPurchaseProduct(productById, purchaseId);
                        productManager.removeProduct(id);
                        System.out.println("Товар добавлен в перечень");
                        id = readInt();
                    } else {
                        System.out.println("Не найден продукт");
                        id = readInt();
                    }
                }
                System.out.println("Товары куплены и добавлены в историю заказов\n");
            } else if (action == 2) {
                productManager.getPurchaseHistory();
                System.out.println("Введите ID покупки");
                int purchaseId = readInt();
                productManager.getProductPurchaseHistory(purchaseId);
            } else if (action == 3) {
                isStart = false;
            }
        }
    }
}

