package questions;

import java.util.HashMap;
import java.util.Map;

enum Coin {
	ONE(1), TWO(2), FIVE(5), TEN(10);

	private final int value;

	Coin(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

class Item {
	private final String code;
	private final String name;
	private final int price;

	public Item(String code, String name, int price) {
		this.code = code;
		this.name = name;
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}
}

class Inventory {
	private final Map<String, Item> itemMap;
	private final Map<String, Integer> stockMap;

	public Inventory() {
		itemMap = new HashMap<String, Item>();
		stockMap = new HashMap<String, Integer>();
	}

	public void addItemToInventory(String code, Item item, int quantity) {
		itemMap.put(code, item);
		stockMap.put(code, quantity);
	}

	public Item getItemFromInventory(String code) {
		return itemMap.get(code);
	}

	public boolean isItemStockAvailable(String code) {
		return stockMap.getOrDefault(code, 0) > 0;
	}

	public void reduceStock(String code, int quantity) {
		stockMap.put(code, stockMap.get(code) - quantity);
	}
}

abstract class VendingMachineState {

	public final VendingMachine machine;

	public VendingMachineState(VendingMachine machine) {
		this.machine = machine;
	}

	abstract void insertCoin(Coin coin);

	abstract void selectItem(String code);

	abstract void dispenseItem();
}


class IdleState extends VendingMachineState {

	public IdleState(VendingMachine vendingMachine) {
		super(vendingMachine);
	}

	@Override
	public void insertCoin(Coin coin) {
		System.out.println("Please select an item before inserting money.");
	}

	@Override
	public void selectItem(String code) {
		if (!machine.getInventory().isItemStockAvailable(code)) {
			System.out.println("Item not available.");
			return;
		}
		machine.setSelectedItemCode(code);
		machine.setSelectedItem(machine.getInventory().getItemFromInventory(code));
		machine.changeState(new ItemSelectedState(machine));
		System.out.println("Item selected: " + code);
	}

	@Override
	public void dispenseItem() {
		System.out.println("No item selected.");
	}
}


class ItemSelectedState extends VendingMachineState {

	public ItemSelectedState(VendingMachine vendingMachine) {
		super(vendingMachine);
	}

	@Override
	public void insertCoin(Coin coin) {
		machine.addBalance(coin.getValue());
		System.out.println("Inserted coin: " + coin.getValue() + ". Current balance: " + machine.getCurrentBalance());
		int selectedItemPrice = machine.getSelectedItem().getPrice();
		if (machine.getCurrentBalance() >= selectedItemPrice) {
			machine.changeState(new MoneyInsertedState(machine));
			machine.getState().dispenseItem();
		} else {
			System.out.println("Please insert more coins. Item price: " + selectedItemPrice);
		}
	}

	@Override
	public void selectItem(String code) {
		System.out.println("Item already selected.");
	}

	@Override
	public void dispenseItem() {
		System.out.println("No item selected.");
	}
}

class MoneyInsertedState extends VendingMachineState {

	public MoneyInsertedState(VendingMachine vendingMachine) {
		super(vendingMachine);
	}

	@Override
	public void insertCoin(Coin coin) {
		System.out.println("Already received full amount.");
	}

	@Override
	public void selectItem(String code) {
		System.out.println("Item already selected.");
	}

	@Override
	public void dispenseItem() {
		machine.changeState(new DispensingState(machine));
		machine.dispenseItem();
	}
}


class DispensingState extends VendingMachineState {

	public DispensingState(VendingMachine vendingMachine) {
		super(vendingMachine);
	}

	@Override
	public void insertCoin(Coin coin) {
		System.out.println("Dispensing in progress. Please wait.");
	}

	@Override
	public void selectItem(String code) {
		System.out.println("Dispensing in progress. Please wait.");
	}

	@Override
	public void dispenseItem() {
		System.out.println("Dispensing item: " + machine.getSelectedItem().getName());
	}
}


class VendingMachine {

	private static volatile VendingMachine instance;
	private final Inventory inventory;
	private VendingMachineState state;
	private String selectedItemCode;
	private Item selectedItem;
	private int currentBalance = 0;

	public VendingMachine() {
		this.inventory = new Inventory();
		this.state = new IdleState(this);
	}

	public static VendingMachine getInstance() {
		if (instance == null) {
			synchronized (VendingMachine.class) {
				if (instance == null) {
					instance = new VendingMachine();
				}
			}
		}
		return instance;
	}

	public void dispenseItem() {
		if (selectedItem == null) {
			System.out.println("No item selected.");
			return;
		}
		int price = selectedItem.getPrice();
		if (currentBalance >= price) {
			inventory.reduceStock(selectedItemCode, 1);
			currentBalance -= price;
			System.out.println("Dispensing item: " + selectedItem.getName());
			System.out.println("Remaining balance: " + currentBalance);
			if (currentBalance > 0) {
				System.out.println("Returning change: " + currentBalance);
			}
			reset();
			changeState(new IdleState(this));
		} else {
			System.out.println("Insufficient balance. Please insert more coins.");
		}
	}

	public void reset() {
		currentBalance = 0;
		selectedItem = null;
		selectedItemCode = null;
	}

	public void insertCoin(Coin coin) {
		state.insertCoin(coin);
	}

	public void dispense() {
		state.dispenseItem();
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void selectItemFromMachine(String code){
		state.selectItem(code);
	}

	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	public void changeState(VendingMachineState state) {
		this.state = state;
	}

	public VendingMachineState getState() {
		return state;
	}

	public String getSelectedItemCode() {
		return selectedItemCode;
	}

	public void setSelectedItemCode(String selectedItemCode) {
		this.selectedItemCode = selectedItemCode;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getCurrentBalance() {
		return currentBalance;
	}

	public void addBalance(int amount) {
		this.currentBalance += amount;
	}
}

public class VendingMachineClient {
	static void main() {
		VendingMachine vendingMachine = VendingMachine.getInstance();

		// Add products to the inventory
		vendingMachine.getInventory().addItemToInventory("A1", new Item("A1", "Coke", 25), 25);
		vendingMachine.getInventory().addItemToInventory("A2", new Item("A2", "Pepsi", 25), 25);
		vendingMachine.getInventory().addItemToInventory("B1", new Item("B1", "Water", 7), 10);

		// Select a product
		System.out.println("\n--- Step 1: Select an item ---");
		vendingMachine.selectItemFromMachine("A1");

		// Insert coins
		System.out.println("\n--- Step 2: Insert coins ---");
		vendingMachine.insertCoin(Coin.TEN); // 10
		vendingMachine.insertCoin(Coin.TEN); // 10
		vendingMachine.insertCoin(Coin.FIVE); // 5

		// Dispense the product
		System.out.println("\n--- Step 3: Dispense item ---");
		vendingMachine.dispense(); // Should dispense Coke

		// Select another item
		System.out.println("\n--- Step 4: Select another item ---");
		vendingMachine.selectItemFromMachine("B1");

		// Insert more amount
		System.out.println("\n--- Step 5: Insert more than needed ---");
		vendingMachine.insertCoin(Coin.TEN); // 10

		// Try to dispense the product
		System.out.println("\n--- Step 6: Dispense and return change ---");
		vendingMachine.dispense();
	}
}
