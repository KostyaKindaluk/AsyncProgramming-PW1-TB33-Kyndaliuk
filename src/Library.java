import java.util.ArrayList;
import java.util.List;


public class Library
{
    private final List<Book> books = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private volatile boolean open = true;

    public Library() {}

    public void addBook(Book book)
    {
        books.add(book);
    }

    public void addStudent(Student s)
    {
        students.add(s);
    }

    public List<Book> getBooks()
    {
        return books;
    }

    public List<Student> getStudents()
    {
        return students;
    }

    public synchronized void openLibrary()
    {
        open = true;
        System.out.println("Бібліотека відчинена.");
    }

    public synchronized void closeLibrary()
    {
        open = false;
        System.out.println("Бібліотека зачинена. Всі потоки-студенти будуть отримані переривання (щоб скасувати очікування).");
        for (Student s : students)
        {
            Thread t = s.getThread();
            if (t != null && t.isAlive())
            {
                t.interrupt();
            }
        }
    }

    public boolean isOpen()
    {
        return open;
    }

    public void borrowBook(int bookIndex, Student student)
    {
        if (!isOpen())
        {
            System.out.printf("[%s] Бібліотека зачинена — %s не може взяти книгу.%n", now(), student.getName());
            return;
        }
        if (bookIndex < 0 || bookIndex >= books.size())
        {
            System.out.printf("[%s] Невірний індекс книги.%n", now());
            return;
        }

        Book book = books.get(bookIndex);
        System.out.printf("[%s] %s намагається взяти книгу \"%s\"... (поточний стан потоку: %s)%n",
                now(), student.getName(), book.getTitle(), student.getThread().getState());


        try
        {
            //семафор.acquire() - отримує ресурс у семафора
            book.borrow(student.getName());
            student.addBookIndex(bookIndex);
            System.out.printf("[%s] Успіх: %s взяв(ла) \"%s\". (Доступно зараз: %d/%d)%n",
                    now(), student.getName(), book.getTitle(), book.availableCopies(), book.getTotalCopies());
        }
        catch (InterruptedException e)
        {
            System.out.printf("[%s] %s перервано під час очікування книги \"%s\". Операцію скасовано.%n",
                    now(), student.getName(), book.getTitle());
            Thread.currentThread().interrupt();
        }
        catch (Exception ex)
        {
            System.out.printf("[%s] Помилка при спробі взяти книгу: %s%n", now(), ex.getMessage());
        }
    }

    public void returnBook(int bookIndex, Student student)
    {
        if (!isOpen())
        {
            System.out.printf("[%s] Бібліотека зачинена — %s не може повернути книгу зараз.%n", now(), student.getName());
            return;
        }
        if (bookIndex < 0 || bookIndex >= books.size())
        {
            System.out.printf("[%s] Невірний індекс книги.%n", now());
            return;
        }

        Book book = books.get(bookIndex);
        System.out.printf("[%s] %s намагається повернути книгу \"%s\"...%n", now(), student.getName(), book.getTitle());
        try
        {
            //семафор.release() - повертає ресурс семафору
            boolean ok = book.giveBack(student.getName());
            if (ok)
            {
                student.removeBookIndex(bookIndex);
                System.out.printf("[%s] Успіх: %s повернув(ла) \"%s\". (Доступно зараз: %d/%d)%n",
                        now(), student.getName(), book.getTitle(), book.availableCopies(), book.getTotalCopies());
            }
            else
            {
                System.out.printf("[%s] %s не мав(мала) цю книгу — повернення неможливе.%n", now(), student.getName());
            }
        }
        catch (Exception ex)
        {
            System.out.printf("[%s] Помилка при поверненні книги: %s%n", now(), ex.getMessage());
        }
    }

    private String now()
    {
        return java.time.LocalTime.now().withNano(0).toString();
    }
}
