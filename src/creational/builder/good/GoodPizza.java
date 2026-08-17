package creational.builder.good;

class Pizza {
    private final String size;
    private final String ketchup;
    private final String veggies;
    private final String sauce;

    public Pizza(PizzaBuilder builder) {
        this.size = builder.size;
        this.ketchup = builder.ketchup;
        this.veggies = builder.veggies;
        this.sauce = builder.sauce;
    }

    static class PizzaBuilder {
        private String size;
        private String ketchup;
        private String veggies;
        private String sauce;

        public PizzaBuilder(String size) {
            this.size = size;
        }

        public PizzaBuilder ketchup(String ketchup) {
            this.ketchup = ketchup;
            return this;
        }

        public PizzaBuilder veggies(String veggies) {
            this.veggies = veggies;
            return this;
        }

        public PizzaBuilder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }

}

public class GoodPizza {
    static void main() {
        Pizza pizzaWithoutAnything = new Pizza.PizzaBuilder("XL").build();
        Pizza pizzaWithSauce = new Pizza.PizzaBuilder("XL").sauce("Chili sauce").build();
    }

}
