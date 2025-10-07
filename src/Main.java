import java.util.List;
import java.util.Scanner;


public class Main
{
    public static void main(String[] args)
    {
        Library library = new Library();

        library.addBook(new Book("Java. Повне керівництво", 3));
        library.addBook(new Book("Алгоритми: мистецтво і стиль", 2));
        library.addBook(new Book("Операційні системи: концепції", 1));

        Student s1 = new Student("Олег");
        Student s2 = new Student("Марія");
        Student s3 = new Student("Іван");

        library.addStudent(s1);
        library.addStudent(s2);
        library.addStudent(s3);

        s1.start();
        s2.start();
        s3.start();

        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        while (!exit)
        {
            printMenu();
            String line = sc.nextLine().trim();
            switch (line)
            {
                case "1" -> library.openLibrary();
                case "2" -> library.closeLibrary();
                case "3" -> listStudents(library);
                case "4" -> listBooks(library);
                case "5" -> studentViewTaken(library, sc);
                case "6" -> studentTakeBook(library, sc);
                case "7" -> studentReturnBook(library, sc);
                case "0" -> exit = true;
                default -> System.out.println("Невірна опція, оберіть зі списку.");
            }
        }

        System.out.println("Завершення роботи програми: зупинка студентських потоків...");
        for (Student s : library.getStudents())
        {
            s.stopAndJoin();
        }
        System.out.println("Програма завершилася!");
        sc.close();
    }

    private static void printMenu()
    {
        System.out.println("\n=== Меню ===");
        System.out.println("1 - Відчинити бібліотеку");
        System.out.println("2 - Зачинити бібліотеку");
        System.out.println("3 - Подивитися список студентів");
        System.out.println("4 - Подивитися список книг");
        System.out.println("5 - Подивитися взяті книги студента");
        System.out.println("6 - Взяти книгу від імені студента");
        System.out.println("7 - Повернути книгу від імені студента");
        System.out.println("0 - Вихід");
        System.out.print("Оберіть опцію: ");
    }

    private static void listStudents(Library lib)
    {
        List<Student> students = lib.getStudents();
        System.out.println("Список студентів:");
        for (int i = 0; i < students.size(); i++)
        {
            Student s = students.get(i);
            System.out.printf("%d) %s — стан потоку: %s%n", i, s.getName(), s.getThread().getState());
        }
    }

    private static void listBooks(Library lib)
    {
        List<Book> books = lib.getBooks();
        System.out.println("Список книг (індекс у списку):");
        for (int i = 0; i < books.size(); i++)
        {
            System.out.printf("%d) %s%n", i, books.get(i));
        }
    }

    private static Student chooseStudent(Library lib, Scanner sc)
    {
        listStudents(lib);
        System.out.print("Введіть індекс студента: ");
        String s = sc.nextLine().trim();
        try
        {
            int idx = Integer.parseInt(s);
            List<Student> students = lib.getStudents();
            if (idx < 0 || idx >= students.size())
            {
                System.out.println("Невірний індекс студента.");
                return null;
            }
            return students.get(idx);
        }
        catch (NumberFormatException ex)
        {
            System.out.println("Некоректний ввід.");
            return null;
        }
    }

    private static int chooseBookIndex(Library lib, Scanner sc)
    {
        listBooks(lib);
        System.out.print("Введіть індекс книги: ");
        String s = sc.nextLine().trim();
        try
        {
            int idx = Integer.parseInt(s);
            if (idx < 0 || idx >= lib.getBooks().size())
            {
                System.out.println("Невірний індекс книги.");
                return -1;
            }
            return idx;
        }
        catch (NumberFormatException ex)
        {
            System.out.println("Некоректний ввід.");
            return -1;
        }
    }

    private static void studentViewTaken(Library lib, Scanner sc)
    {
        Student st = chooseStudent(lib, sc);
        if (st == null) return;
        List<Integer> taken = st.getMyBooksSnapshot();
        if (taken.isEmpty())
        {
            System.out.printf("%s наразі не має жодних книг.%n", st.getName());
            return;
        }
        System.out.printf("%s має такі книги:%n", st.getName());
        for (Integer idx : taken)
        {
            Book b = lib.getBooks().get(idx);
            System.out.printf(" - %s (індекс %d)%n", b.getTitle(), idx);
        }
    }

    private static void studentTakeBook(Library lib, Scanner sc)
    {
        Student st = chooseStudent(lib, sc);
        if (st == null) return;
        int bookIdx = chooseBookIndex(lib, sc);
        if (bookIdx < 0) return;

        st.submitTask(() -> lib.borrowBook(bookIdx, st));
        System.out.printf("Дія 'взяти книгу' додана у чергу для %s. Перевірте вивід.%n", st.getName());
    }

    private static void studentReturnBook(Library lib, Scanner sc)
    {
        Student st = chooseStudent(lib, sc);
        if (st == null) return;
        int bookIdx = chooseBookIndex(lib, sc);
        if (bookIdx < 0) return;

        st.submitTask(() -> lib.returnBook(bookIdx, st));
        System.out.printf("Дія 'повернути книгу' додана у чергу для %s.%n", st.getName());
    }
}