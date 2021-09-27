package Shop;

import java.util.List;
import java.util.Random;

public class ProductGenerator {
    private int maxId;
    private final List<String> productNouns = List.of("Meat", "Cheese", "Bread", "Cake", "Wine", "Coffee", "Chips", "Soda", "Spaghetti", "PopCorn");
    private final List<String> productAdjectives = List.of("Wonderful", "Delicious", "Awesome", "Tasty", "Fresh", "Rotten", "Bitter", "Dirty", "Large", "Small");


    public ProductGenerator(int maxId) {
        this.maxId = maxId;
    }

    public Product generate() {
        maxId++;
        Random random = new Random();
        int price = random.nextInt(30) + 1;
        String name = productAdjectives.get(random.nextInt(productAdjectives.size())) + " " + productNouns.get(random.nextInt(productNouns.size()));
        return new Product(maxId, name, price);
    }
}
