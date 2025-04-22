package Lab_5;

import java.util.*;
import java.util.stream.Collectors;

class Book {
    private String title;
    private String author;
    private String genre;
    private int publicationYear;

    public Book(String title, String author, String genre, int year) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = year;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getPublicationYear() { return publicationYear; }

    @Override
    public String toString() {
        return String.format("{\"title\": \"%s\", \"author\": \"%s\", \"genre\": \"%s\", \"publicationYear\": %d}", title, author, genre, publicationYear);
    }
}

interface Searchable<T> {
    List<T> search(String keyword);
}

class Library<T extends Book> implements Searchable<T> {
    private List<T> books = new ArrayList<>();
    private Set<String> genres = new HashSet<>();
    private Map<String, List<T>> authorMap = new HashMap<>();

    public void addBook(T book) {
        books.add(book);
        genres.add(book.getGenre());
        authorMap.computeIfAbsent(book.getAuthor(), k -> new ArrayList<>()).add(book);
    }

    public void removeBook(T book) {
        books.remove(book);
        authorMap.getOrDefault(book.getAuthor(), new ArrayList<>()).remove(book);
    }

    public List<T> getBooks() {
        return books;
    }

    public List<T> filterByGenre(String genre) {
        return books.stream().filter(book -> book.getGenre().equalsIgnoreCase(genre)).collect(Collectors.toList());
    }

    public List<T> filterByAuthor(String author) {
        return authorMap.getOrDefault(author, new ArrayList<>());
    }

    @Override
    public List<T> search(String keyword) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void displayAllBooks() {
        for (T book : books) {
            System.out.println(book);
        }
    }

    public void sortBooksByTitle() {
        books.sort(Comparator.comparing(Book::getTitle));
    }

    public String recommendBook(String category) {
        return switch (category.toLowerCase()) {
            case "science" -> "Try 'A Brief History of Time' by Stephen Hawking.";
            case "fiction" -> "Try '1984' by George Orwell.";
            case "biography" -> "Try 'A Beautiful Mind' by Sylvia Nasar.";
            default -> "No recommendations available.";
        };
    }
}

// Borrower class
class Borrower {
    private String name;
    private List<Book> borrowedBooks = new ArrayList<>();

    public Borrower(String name) {
        this.name = name;
    }

    public void borrow(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    @Override
    public String toString() {
        return String.format("{\"name\": \"%s\", \"borrowedBooks\": %s}", name, borrowedBooks.stream().map(Book::getTitle).toList());
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library<Book> library = new Library<>();

        // Test Case 1: Adding Books
        Book book1 = new Book("1984", "George Orwell", "Fiction", 1949);
        Book book2 = new Book("A Brief History of Time", "Stephen Hawking", "Science", 1988);
        library.addBook(book1);
        library.addBook(book2);
        System.out.println("Library contains " + library.getBooks().size() + " books.");

        // Test Case 2: Filtering by Genre
        System.out.println("Books in Fiction:");
        library.filterByGenre("Fiction").forEach(System.out::println);

        // Test Case 3: Filtering by Author
        System.out.println("Books by Stephen Hawking:");
        library.filterByAuthor("Stephen Hawking").forEach(System.out::println);

        // Test Case 4: Searching by Keyword
        System.out.println("Search for 'Time':");
        library.search("Time").forEach(System.out::println);

        // Test Case 5: Add more books + sort
        Book book3 = new Book("A Beautiful Mind", "Sylvia Nasar", "Biography", 1998);
        library.addBook(book3);
        library.sortBooksByTitle();
        System.out.println("Sorted Books by Title:");
        library.displayAllBooks();

        // Test Case 6: Recommendations
        System.out.println("Recommendation for Science: " + library.recommendBook("Science"));

        // Test Case 7: Borrower borrows a book
        Borrower alice = new Borrower("Alice");
        alice.borrow(book1);
        System.out.println("After borrowing 1984:");
        System.out.println(alice);

        // Test Case 8: Borrower returns the book
        alice.returnBook(book1);
        System.out.println("After returning 1984:");
        System.out.println(alice);
    }
}

