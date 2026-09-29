package questions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


class ActionNotAvailableInStateException extends RuntimeException {
	public ActionNotAvailableInStateException(String message) {
		super(message);
	}
}

enum ItemType {
	BOOK,
	MAGAZINE
}

class Member {
	private final String id;
	private final String name;
	private final List<Loan> loans;

	public Member(String id, String name) {
		this.id = id;
		this.name = name;
		loans = new ArrayList<>();
	}

	public void update(LibraryItem item) {
		System.out.println("Notification for " + name + "For the item " + item.getTitle());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Loan> getLoans() {
		return loans;
	}

	public void addLoan(Loan loan) {
		loans.add(loan);
	}

	public void removeLoan(Loan loan) {
		loans.remove(loan);
	}
}

abstract class LibraryItem {
	private final String id;
	private final String title;
	protected final List<LibraryItemCopy> copies;
	private final List<Member> observers;

	public LibraryItem(String id, String title) {
		this.id = id;
		this.title = title;
		copies = new ArrayList<>();
		observers = new CopyOnWriteArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public List<LibraryItemCopy> getCopies() {
		return copies;
	}

	public List<Member> getObservers() {
		return observers;
	}

	public boolean hasObservers() {
		return !observers.isEmpty();
	}

	public void addCopy(LibraryItemCopy copy) {
		copies.add(copy);
	}

	public void addNotifier(Member member) {
		observers.add(member);
	}

	public void removeObserver(Member member) {
		observers.remove(member);
	}

	public void notifyObservers() {
		System.out.println("Notifying " + observers.size() + " observers for '" + title + "'...");
		observers.forEach(member -> member.update(this));
	}

	public abstract String getAuthorOPublisher();

	public long getAvailableCopyCount() {
		return copies.stream().filter(LibraryItemCopy::isAvailable).count();
	}

}

class Book extends LibraryItem {
	private final String author;

	public Book(String id, String title, String author) {
		super(id, title);
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	@Override
	public String getAuthorOPublisher() {
		return author;
	}
}


class Magazine extends LibraryItem {
	private final String publisher;

	public Magazine(String id, String title, String publisher) {
		super(id, title);
		this.publisher = publisher;
	}

	@Override
	public String getAuthorOPublisher() {
		return publisher;
	}
}

class LibraryItemFactory {
	public static LibraryItem createLibraryItem(ItemType itemType, String id, String title, String authorOrPublisher) {
		return switch (itemType) {
			case BOOK -> new Book(id, title, authorOrPublisher);
			case MAGAZINE -> new Magazine(id, title, authorOrPublisher);
			default -> throw new IllegalArgumentException("Unknown item type.");
		};
	}
}

class LibraryItemCopy {

	private final String id;
	private final LibraryItem item;
	private LibraryItemState currentState;

	public LibraryItemCopy(String id, LibraryItem item) {
		this.id = id;
		this.item = item;
		item.addCopy(this);
		this.currentState = new AvailableState();
	}

	public String getId() {
		return id;
	}

	public LibraryItem getItem() {
		return item;
	}

	public LibraryItemState getCurrentState() {
		return currentState;
	}

	public void changeCurrentState(LibraryItemState newState) {
		this.currentState = newState;
	}

	public void checkout(Member member) {
		currentState.checkout(member, this);
	}

	public void returnItem(LibraryItemCopy itemCopy) {
		currentState.returnItem(itemCopy);
	}

	public void placeHold(Member member) {
		currentState.placeHold(member, this);
	}

	public boolean isAvailable() {
		return currentState instanceof AvailableState;
	}
}

class Loan {

	private final LibraryItemCopy itemCopy;
	private final Member member;
	private final LocalDateTime checkoutTime;

	public Loan(LibraryItemCopy itemCopy, Member member) {
		this.itemCopy = itemCopy;
		this.member = member;
		this.checkoutTime = LocalDateTime.now();
	}

	public LibraryItemCopy getItemCopy() {
		return itemCopy;
	}

	public Member getMember() {
		return member;
	}

	public LocalDateTime getCheckoutTime() {
		return checkoutTime;
	}
}

interface LibraryItemState {
	void checkout(Member member, LibraryItemCopy copy);

	void returnItem(LibraryItemCopy copy);

	void placeHold(Member member, LibraryItemCopy copy);
}

class AvailableState implements LibraryItemState {

	@Override
	public void checkout(Member member, LibraryItemCopy copy) {
		TransactionService.getInstance().createLoan(member, copy);
		copy.changeCurrentState(new CheckedOutState());
		System.out.println(copy.getItem().getTitle() + " has been checked-out by " + member.getName());
	}

	@Override
	public void returnItem(LibraryItemCopy copy) {
		throw new ActionNotAvailableInStateException("Action not available in state " + copy.getItem().getTitle());
	}

	@Override
	public void placeHold(Member member, LibraryItemCopy copy) {
		throw new ActionNotAvailableInStateException("Action not available in state " + copy.getItem().getTitle());
	}
}

class CheckedOutState implements LibraryItemState {
	@Override
	public void checkout(Member member, LibraryItemCopy copy) {
		throw new ActionNotAvailableInStateException("Action not available in state " + copy.getItem().getTitle());
	}

	@Override
	public void returnItem(LibraryItemCopy copy) {
		TransactionService.getInstance().endLoan(copy);
		System.out.println(copy.getId() + " has been returned");
		if (copy.getItem().hasObservers()) {
			copy.getItem().notifyObservers();
			copy.changeCurrentState(new OnHoldState());
		} else {
			copy.changeCurrentState(new AvailableState());
		}
	}

	@Override
	public void placeHold(Member member, LibraryItemCopy copy) {
		copy.getItem().addNotifier(member);
		System.out.println(member.getName() + " has placed a hold on item " + copy.getItem().getTitle());
	}
}

class OnHoldState implements LibraryItemState {
	@Override
	public void checkout(Member member, LibraryItemCopy copy) {
		if (copy.getItem().getObservers().contains(member)) {
			TransactionService.getInstance().createLoan(member, copy);
			copy.getItem().removeObserver(member);
			copy.changeCurrentState(new CheckedOutState());
			System.out.println("Hold fulfilled. " + copy.getId() + " checked out by " + member.getName());
		} else {
			System.out.println("This item is on hold for another member.");
		}
	}

	@Override
	public void returnItem(LibraryItemCopy copy) {
		throw new ActionNotAvailableInStateException("Action not available in state " + copy.getItem().getTitle());
	}

	@Override
	public void placeHold(Member member, LibraryItemCopy copy) {
		if (!copy.getItem().getObservers().contains(member)) {
			copy.getItem().addNotifier(member);
		}
		System.out.println(member.getName() + " has placed a hold on item " + copy.getItem().getTitle());
	}
}


class TransactionService {

	private static TransactionService instance;
	private final Map<String, Loan> loansData;

	public TransactionService() {
		loansData = new HashMap<>();
	}

	public static TransactionService getInstance() {
		if (instance == null) {
			synchronized (TransactionService.class) {
				if (instance == null) {
					instance = new TransactionService();
				}
			}
		}
		return instance;
	}

	public void createLoan(Member member, LibraryItemCopy copy) {
		if (loansData.containsKey(copy.getId())) {
			throw new IllegalStateException("This copy is already on loan.");
		}
		Loan loan = new Loan(copy, member);
		member.addLoan(loan);
		loansData.put(copy.getId(), loan);
	}

	public void endLoan(LibraryItemCopy copy) {
		if (!loansData.containsKey(copy.getId())) {
			throw new IllegalStateException("No loan details for this copy");
		}
	}
}

class LibraryManagementFacade {
	private static volatile LibraryManagementFacade instance;
	private final Map<String, LibraryItem> catalogue;
	private final Map<String, Member> members;
	private final Map<String, LibraryItemCopy> copies;

	public LibraryManagementFacade() {
		this.catalogue = new HashMap<>();
		this.members = new HashMap<>();
		this.copies = new HashMap<>();
	}

	public static LibraryManagementFacade getInstance() {
		if (instance == null) {
			synchronized (LibraryManagementFacade.class) {
				if (instance == null) {
					instance = new LibraryManagementFacade();
				}
			}
		}
		return instance;
	}

	public Member addMember(String id, String name) {
		Member member = new Member(id, name);
		this.members.put(id, member);
		System.out.println("Member " + id + " has been added.");
		return member;
	}

	public List<LibraryItemCopy> addItemAndCopies(ItemType item, String id, String title, String authorOrPublisher, int numOfCopies) {
		LibraryItem libraryItem = LibraryItemFactory.createLibraryItem(item, id, title, authorOrPublisher);
		List<LibraryItemCopy> itemCopies = new ArrayList<>();
		catalogue.put(id, libraryItem);
		for (int i = 0; i < numOfCopies; i++) {
			String copyItemId = id + "-c" + (i + 1);
			LibraryItemCopy libraryItemCopy = new LibraryItemCopy(copyItemId, libraryItem);
			copies.put(copyItemId, libraryItemCopy);
			itemCopies.add(libraryItemCopy);
		}
		System.out.println("Added " + numOfCopies + " copies of '" + title + "'");
		return itemCopies;
	}


	public void checkout(String memberId, String copyId) {
		Member member = members.get(memberId);
		LibraryItemCopy copy = copies.get(copyId);
		if (member != null && copy != null) {
			copy.checkout(member);
		} else {
			throw new ActionNotAvailableInStateException("Action not available in state " + copyId);
		}
	}

	public void returnItem(String copyId) {
		LibraryItemCopy copy = copies.get(copyId);
		if (copy != null) {
			copy.returnItem(copy);
		} else {
			throw new ActionNotAvailableInStateException("Action not available in state " + copyId);
		}
	}

	public void placeHold(String memberId, String itemId) {
		Member member = members.get(memberId);
		LibraryItem libraryItem = catalogue.get(itemId);
		if (member != null && libraryItem != null) {
			libraryItem.getCopies().stream()
					.filter(itemCopy -> !itemCopy.isAvailable())
					.findFirst()
					.ifPresent(availableState -> availableState
							.placeHold(member));
		}
	}

	public void printCatalog() {
		System.out.println("\n--- Library Catalog ---");
		catalogue.values().forEach(item -> System.out.printf("ID: %s, Title: %s, Author/Publisher: %s, Available: %d\n",
				item.getId(), item.getTitle(), item.getAuthorOPublisher(), item.getAvailableCopyCount()));
		System.out.println("-----------------------\n");
	}
}


public class LibraryManagement {
	static void main() {
		LibraryManagementFacade library = LibraryManagementFacade.getInstance();

		// --- Setup: Add items and members using the Facade ---
		System.out.println("=== Setting up the Library ===");

		List<LibraryItemCopy> hobbitCopies = library.addItemAndCopies(ItemType.BOOK, "B001", "The Hobbit", "J.R.R. Tolkien", 2);
		List<LibraryItemCopy> duneCopies = library.addItemAndCopies(ItemType.BOOK, "B002", "Dune", "Frank Herbert", 1);
		List<LibraryItemCopy> natGeoCopies = library.addItemAndCopies(ItemType.MAGAZINE, "M001", "National Geographic", "NatGeo Society", 3);

		Member alice = library.addMember("MEM01", "Alice");
		Member bob = library.addMember("MEM02", "Bob");
		Member charlie = library.addMember("MEM03", "Charlie");
		library.printCatalog();

		// --- Scenario 1: Searching (Strategy Pattern) ---
//		System.out.println("\n=== Scenario 1: Searching for Items ===");
//		System.out.println("Searching for title 'Dune':");
//		library.search("Dune", new SearchByTitleStrategy())
//				.forEach(item -> System.out.println("Found: " + item.getTitle()));
//		System.out.println("\nSearching for author 'Tolkien':");
//		library.search("Tolkien", new SearchByAuthorStrategy())
//				.forEach(item -> System.out.println("Found: " + item.getTitle()));

		// --- Scenario 2: Checkout and Return (State Pattern) ---
		System.out.println("\n\n=== Scenario 2: Checkout and Return ===");
		library.checkout(alice.getId(), hobbitCopies.get(0).getId()); // Alice checks out The Hobbit copy 1
		library.checkout(bob.getId(), duneCopies.get(0).getId()); // Bob checks out Dune copy 1
		library.printCatalog();

		System.out.println("Attempting to checkout an already checked-out book:");
		library.checkout(charlie.getId(), hobbitCopies.get(0).getId()); // Charlie fails to check out The Hobbit copy 1

		System.out.println("\nAlice returns The Hobbit:");
		library.returnItem(hobbitCopies.get(0).getId());
		library.printCatalog();

		// --- Scenario 3: Holds and Notifications (Observer Pattern) ---
		System.out.println("\n\n=== Scenario 3: Placing a Hold ===");
		System.out.println("Dune is checked out by Bob. Charlie places a hold.");
		library.placeHold(charlie.getId(), "B002"); // Charlie places a hold on Dune

		System.out.println("\nBob returns Dune. Charlie should be notified.");
		library.returnItem(duneCopies.get(0).getId()); // Bob returns Dune

		System.out.println("\nCharlie checks out the book that was on hold for him.");
		library.checkout(charlie.getId(), duneCopies.get(0).getId());

		System.out.println("\nTrying to check out the same on-hold item by another member (Alice):");
		library.checkout(alice.getId(), duneCopies.get(0).getId()); // Alice fails, it's checked out by Charlie now.

		library.printCatalog();
	}

}
