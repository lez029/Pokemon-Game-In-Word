package Entity;
public enum Nature {
        // Let 0 = HP, 1 = Attack, 2 = Defense,
        // 3 = SA, 4 = SD, 5 = Speed
        HARDY(-1, -1, null, null),
        LONELY(1, 2, Flavor.SPICY, Flavor.SOUR),
        Adamant(1, 3, Flavor.SPICY, Flavor.DRY),
        Naughty(1, 4, Flavor.SPICY, Flavor.BITTER),
        BRAVE(1, 5, Flavor.SPICY, Flavor.SWEET),
        BOLD(2, 1, Flavor.SOUR, Flavor.SPICY),
        DOCILE(-1, -1, null, null),
        IMPISH(2, 3, Flavor.SOUR, Flavor.DRY),
        LAX(2, 4, Flavor.SOUR, Flavor.BITTER),
        RELAXED(2, 5, Flavor.SOUR, Flavor.SWEET),
        MODEST(3, 1, Flavor.DRY, Flavor.SPICY),
        MILD(3, 2, Flavor.DRY, Flavor.SOUR),
        BASHFUL(-1, -1, null, null),
        RASH(3, 4, Flavor.DRY, Flavor.BITTER),
        QUIET(3, 5, Flavor.DRY, Flavor.SWEET),
        CALM(4, 1, Flavor.BITTER, Flavor.SPICY),
        GENTLE(4, 2, Flavor.BITTER, Flavor.SOUR),
        CAREFUL(4, 3, Flavor.BITTER, Flavor.DRY),
        QUIRKY(-1, -1, null, null),
        Sassy(4, 5, Flavor.BITTER, Flavor.SWEET),
        TIMID(5, 1, Flavor.SWEET, Flavor.SPICY),
        HASTY(5, 2, Flavor.SWEET, Flavor.SOUR),
        JOLLY(5, 3, Flavor.SWEET, Flavor.DRY),
        NAIVE(5, 4, Flavor.SWEET, Flavor.BITTER),
        SERIOUS(-1, -1, null, null);

        private int increase;
        private int decrease;
        private double[] modifier = new double[]{1,1,1,1,1,1};
        private Flavor likeFlavor;
        private Flavor hateFlavor;

        Nature(int increase, int decrease, Flavor likeFlavor, Flavor hateFlavor) {
                this.increase = increase;
                this.decrease = decrease;
                if (increase != -1)
                        this.modifier[increase] = 1.1;
                if (decrease != -1)
                        this.modifier[decrease] = 0.9;
                this.likeFlavor = likeFlavor;
                this.hateFlavor = hateFlavor;
        }

        public int getIncrease() {
                return increase;
        }

        public int getDecrease() {
                return decrease;
        }

        public double[] getModifier() {
                return modifier;
        }
}
