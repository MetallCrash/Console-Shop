package Shop;

import java.io.IOException;

public class Producer implements Runnable{
    Shop shop;

    public Producer(Shop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                shop.produceProduct();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
