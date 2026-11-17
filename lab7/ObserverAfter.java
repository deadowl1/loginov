// Вариант 14 (Логистический центр): Observer для события «сформирована отправка»
import java.util.ArrayList;
import java.util.List;

public class ObserverAfter {

    private static int failedChecks = 0;

    public static void main(String[] args) {
        LogisticsCenter center = new LogisticsCenter("Логистический центр «Гомель-Запад»");

        WarehouseBoard board = new WarehouseBoard();
        DispatchJournal journal = new DispatchJournal();
        ClientSmsNotifier smsNotifier = new ClientSmsNotifier();
        CustomsSystem customsSystem = new CustomsSystem(); // 4-й наблюдатель

        // Подписываем получателей
        center.addListener(board);
        center.addListener(journal);
        center.addListener(smsNotifier);
        center.addListener(customsSystem);

        System.out.println("--- Первое событие: отправка сформирована ---");
        center.shipmentFormed("SHP-2026-01");

        System.out.println("\n--- Проверки после первого события ---");
        check("Журнал получил ровно одну запись", journal.getRecordsCount() == 1);
        check("Все подписчики оповещены",
                board.hasReceived() && journal.hasReceived() && smsNotifier.hasReceived() && customsSystem.hasReceived());

        // Отписываем складское табло
        center.removeListener(board);
        int boardCallsBeforeSecondEvent = board.getCallCount();

        System.out.println("\n--- Второе событие (после отписки табло) ---");
        center.shipmentFormed("SHP-2026-02");

        check("После отписки табло больше не получает уведомления", board.getCallCount() == boardCallsBeforeSecondEvent);

        if (failedChecks > 0) {
            throw new AssertionError("Не пройдено проверок: " + failedChecks);
        }
        System.out.println("\nВсе проверки пройдены успешно.");
    }

    static void check(String checkName, boolean passed) {
        System.out.println((passed ? "[OK]   " : "[FAIL] ") + checkName);
        if (!passed) {
            failedChecks++;
        }
    }
}

// --- Участники паттерна Observer ---

interface ShipmentListener {
    void onShipmentFormed(String centerName, String shipmentId);
}

class LogisticsCenter {
    private final String centerName;
    private final List<ShipmentListener> listeners = new ArrayList<>();

    LogisticsCenter(String centerName) {
        this.centerName = centerName;
    }

    public void addListener(ShipmentListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ShipmentListener listener) {
        listeners.remove(listener);
    }

    public void shipmentFormed(String shipmentId) {
        System.out.println("-> " + centerName + ": сформирована отправка " + shipmentId);
        for (ShipmentListener listener : listeners) {
            listener.onShipmentFormed(centerName, shipmentId);
        }
    }
}

class WarehouseBoard implements ShipmentListener {
    private boolean received = false;
    private int callCount = 0;

    public void onShipmentFormed(String centerName, String shipmentId) {
        received = true;
        callCount++;
        System.out.println("  [Складское табло] Отправка " + shipmentId + " готова к отгрузке (" + centerName + ")");
    }

    public boolean hasReceived() { return received; }
    public int getCallCount() { return callCount; }
}

class DispatchJournal implements ShipmentListener {
    private final List<String> records = new ArrayList<>();

    public void onShipmentFormed(String centerName, String shipmentId) {
        records.add(centerName + ": " + shipmentId);
        System.out.println("  [Журнал отправок] Записана отправка " + shipmentId);
    }

    public int getRecordsCount() { return records.size(); }
    public boolean hasReceived() { return !records.isEmpty(); }
}

class ClientSmsNotifier implements ShipmentListener {
    private boolean received = false;

    public void onShipmentFormed(String centerName, String shipmentId) {
        received = true;
        System.out.println("  [SMS-рассылка] Клиенту отправлено уведомление по заказу " + shipmentId);
    }

    public boolean hasReceived() { return received; }
}

class CustomsSystem implements ShipmentListener {
    private boolean received = false;

    public void onShipmentFormed(String centerName, String shipmentId) {
        received = true;
        System.out.println("  [Таможня] Электронная накладная на отправку " + shipmentId + " зарегистрирована");
    }

    public boolean hasReceived() { return received; }
}
