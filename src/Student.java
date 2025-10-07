import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Student implements Runnable {
    private final String name;
    private final Thread thread;
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private final List<Integer> myBooks = new ArrayList<>();

    private volatile boolean running = true;

    public Student(String name)
    {
        this.name = name;
        this.thread = new Thread(this, "Student-" + name);
    }

    public void start()
    {
        thread.start();
    }

    public void stopAndJoin()
    {
        running = false;
        thread.interrupt();
        try
        {
            thread.join();
        }
        catch (InterruptedException ignored)
        {
        }
    }

    public Thread getThread()
    {
        return thread;
    }

    public String getName()
    {
        return name;
    }

    public void submitTask(Runnable task) {
        tasks.offer(task);
    }

    public List<Integer> getMyBooksSnapshot()
    {
        synchronized (myBooks)
        {
            return new ArrayList<>(myBooks);
        }
    }

    public void addBookIndex(int index)
    {
        synchronized (myBooks) { myBooks.add(index); }
    }

    public void removeBookIndex(int index)
    {
        synchronized (myBooks) { myBooks.remove((Integer) index); }
    }

    @Override
    public void run()
    {
        while (running) {
            try
            {
                //чекає, коли є задача
                Runnable task = tasks.take();
                task.run();
            }
            catch (InterruptedException e)
            {
                if (!running) break;
            }
            catch (Exception ex)
            {
                System.out.printf("Сталася помилка у дії студента %s: %s%n", name, ex.getMessage());
            }
        }
        System.out.printf("Потік студента %s завершив роботу.%n", name);
    }

    @Override
    public String toString()
    {
        return name + " (потік: " + thread.getState() + ")";
    }
}
