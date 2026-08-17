package creational.builder.bad;

class Pizza {
    private final String size;
    private final String ketchup;
    private final String veggies;
    private final String sauce;

    public Pizza(String size) {
        this(size, null);
    }

    public Pizza(String size, String ketchup) {
        this(size, ketchup, null);
    }

    public Pizza(String size, String ketchup, String veggies) {
        this(size, ketchup, veggies, null);
    }

    public Pizza(String size, String ketchup, String veggies, String sauce) {
        this.size = size;
        this.ketchup = ketchup;
        this.veggies = veggies;
        this.sauce = sauce;
    }
}

public class BadPizza {
    static void main() {
        // Client wants basic pizza
        Pizza pizza = new Pizza("XL");

        // client wants pizza with ketchup
        Pizza pizzaWithKetchup = new Pizza("XL", "Tomato ketchup");

        // client wants pizza without ketchup but with sauce
        Pizza pizzaWithSauce = new Pizza("XL", null, null, "Chili sauce");
    }
}
