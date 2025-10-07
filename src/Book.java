import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;


public class Book {
    private final String title;
    private final int totalCopies;
    private final Semaphore copies;
    private final Set<String> holders = new HashSet<>();

    public Book(String title, int totalCopies)
    {
        if (totalCopies < 1) throw new IllegalArgumentException("Кількість примірників має бути >= 1");
        this.title = title;
        this.totalCopies = totalCopies;
        //семафор
        this.copies = new Semaphore(totalCopies, true);
    }

    public String getTitle()
    {
        return title;
    }

    public int getTotalCopies()
    {
        return totalCopies;
    }

    public int availableCopies()
    {
        return copies.availablePermits();
    }

    public void borrow(String studentName) throws InterruptedException
    {
        //блокує, якщо немає вільних примірників
        copies.acquire();
        synchronized (holders)
        {
            holders.add(studentName);
        }
    }

    public boolean giveBack(String studentName)
    {
        synchronized (holders)
        {
            if (!holders.remove(studentName))
            {
                return false;
            }
        }
        copies.release();
        return true;
    }

    public String holdersString()
    {
        synchronized (holders)
        {
            if (holders.isEmpty()) return "Ніхто не має цієї книги зараз";
            return String.join(", ", holders);
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s — Примірників: %d, Доступно: %d", title, totalCopies, availableCopies());
    }
}