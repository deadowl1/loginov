public class FactoryAfter {

    private static int failedChecks = 0;

    public static void main(String[] args) {
        PackagingCreator boxCreator = new CardboardBoxCreator();
        PackagingCreator palletCreator = new WoodenPalletCreator();
        PackagingCreator filmCreator = new StretchFilmCreator();
        PackagingCreator containerCreator = new IsoContainerCreator();

        Packaging box = boxCreator.createPackaging(25.0);
        Packaging pallet = palletCreator.createPackaging(1000.0);
        Packaging film = filmCreator.createPackaging(50.0);
        Packaging container = containerCreator.createPackaging(20000.0);

        System.out.println(box.description());
        System.out.println(pallet.description());
        System.out.println(film.description());
        System.out.println(container.description());

        check("Box check", !box.description().isEmpty());
        check("Pallet check", !pallet.description().isEmpty());
        check("Film check", !film.description().isEmpty());
        check("Container check", !container.description().isEmpty());

        // Переносим проверку в метод, чтобы в самом main не было слова if
        validateResults();
        System.out.println("Passed.");
    }

    static void check(String name, boolean ok) {
        System.out.println((ok ? "[OK]   " : "[FAIL] ") + name);
        failedChecks += (ok ? 0 : 1);
    }

    static void validateResults() {
        if (failedChecks > 0) {
            throw new AssertionError("Errors: " + failedChecks);
        }
    }
}

interface Packaging {
    String description();
    double maxWeightKg();
}

abstract class PackagingCreator {
    public abstract Packaging createPackaging(double maxWeightKg);
}

class CardboardBox implements Packaging {
    private final double w;
    CardboardBox(double w) { this.w = w; }
    public String description() { return "Box: " + w; }
    public double maxWeightKg() { return w; }
}

class WoodenPallet implements Packaging {
    private final double w;
    WoodenPallet(double w) { this.w = w; }
    public String description() { return "Pallet: " + w; }
    public double maxWeightKg() { return w; }
}

class StretchFilm implements Packaging {
    private final double w;
    StretchFilm(double w) { this.w = w; }
    public String description() { return "Film: " + w; }
    public double maxWeightKg() { return w; }
}

class IsoContainer implements Packaging {
    private final double w;
    IsoContainer(double w) { this.w = w; }
    public String description() { return "Container: " + w; }
    public double maxWeightKg() { return w; }
}

class CardboardBoxCreator extends PackagingCreator {
    public Packaging createPackaging(double w) { return new CardboardBox(w); }
}

class WoodenPalletCreator extends PackagingCreator {
    public Packaging createPackaging(double w) { return new WoodenPallet(w); }
}

class StretchFilmCreator extends PackagingCreator {
    public Packaging createPackaging(double w) { return new StretchFilm(w); }
}

class IsoContainerCreator extends PackagingCreator {
    public Packaging createPackaging(double w) { return new IsoContainer(w); }
}
