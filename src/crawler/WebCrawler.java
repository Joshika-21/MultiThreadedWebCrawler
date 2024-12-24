package crawler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit; // Added import for TimeUnit
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawler {
    private static final String VISITED_URLS_FILE = "visited_urls.ser"; // File for URL persistence
    private ExecutorService executor; // Removed 'final' to allow reassignment
    private final Set<String> visitedUrls = new HashSet<>();
    private final Set<String> failedUrls = new HashSet<>(); // Class-level field for retries
    private final int maxDepth; // Removed default initialization since it is redundant
    private int crawlCount = 0; // Counter to track the number of crawled URLs (kept at the class level)
    public int totalUrlsVisited = 0; // Kept as a field for performance tracking

    public WebCrawler(int threadCount, int maxDepth) {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.maxDepth = maxDepth;
    }

    public void adjustThreadPoolSize(int newThreadCount) {
        executor.shutdownNow(); // Gracefully stop the current thread pool
        executor = Executors.newFixedThreadPool(newThreadCount); // Reassign with new thread count
        System.out.println("Thread pool size adjusted to: " + newThreadCount);
    }

    @SuppressWarnings("unchecked")
    private void loadVisitedUrls() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(VISITED_URLS_FILE))) {
            visitedUrls.addAll((Set<String>) in.readObject());
            System.out.println("Visited URLs loaded from file.");
        } catch (FileNotFoundException e) {
            System.out.println("No saved visited URLs file found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading visited URLs: " + e.getMessage());
        }
    }

    private void saveVisitedUrls() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(VISITED_URLS_FILE))) {
            out.writeObject(visitedUrls);
            System.out.println("Visited URLs saved to file.");
        } catch (Exception e) {
            System.out.println("Error saving visited URLs: " + e.getMessage());
        }
    }

    public static String fetchContent(String urlString) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line).append("\n");
        }
        in.close();
        return content.toString();
    }

    public List<String> extractLinks(String htmlContent) {
        List<String> links = new ArrayList<>();
        Document document = Jsoup.parse(htmlContent);
        Elements linkElements = document.select("a[href]");
        linkElements.forEach(element -> links.add(element.attr("abs:href")));
        return links;
    }

    public void crawl(String startUrl, int depth) {
        if (depth > maxDepth || visitedUrls.contains(startUrl)) {
            System.out.println("Skipping URL: " + startUrl + " (depth: " + depth + ")");
            return;
        }
        totalUrlsVisited++;
        System.out.println("Total URLs visited so far: " + totalUrlsVisited);
        visitedUrls.add(startUrl);
        executor.execute(new CrawlTask(startUrl, depth + 1));

        crawlCount++;
        if (crawlCount % 10 == 0) { // Save visited URLs every 10 crawls
            saveVisitedUrls();
        }
    }

    private class CrawlTask implements Runnable {
        private final String url;
        private final int depth;

        public CrawlTask(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + " crawling: " + url + " at depth " + depth);
                String content = fetchContent(url);
                List<String> links = extractLinks(content);
                links.forEach(link -> crawl(link, depth + 1));
                Thread.sleep(500); // Optional: Pause for politeness
            } catch (Exception e) {
                System.out.println("Failed to fetch or parse URL: " + url);
                if (!failedUrls.contains(url)) {
                    failedUrls.add(url);
                    System.out.println("Retrying URL: " + url);
                    // Check if the executor is still running before re-submitting
                    if (!executor.isShutdown()) {
                        executor.execute(new CrawlTask(url, depth));
                    } else {
                        System.out.println("Executor is shutting down. Skipping retry for URL: " + url);
                    }
                } else {
                    System.out.println("URL permanently failed: " + url);
                }
            }
        }
    }

    public void shutdown() {
        executor.shutdown(); // Initiate shutdown
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) { // Wait for ongoing tasks to finish
                System.out.println("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            System.out.println("Termination interrupted: " + e.getMessage());
        } finally {
            saveVisitedUrls(); // Save visited URLs regardless of shutdown status
        }
    }

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler(5, 5); // Initialize with a thread pool of 5 threads
        crawler.loadVisitedUrls();

        // Clear previously visited URLs for debugging purposes
        crawler.visitedUrls.clear();

        long startTime = System.currentTimeMillis(); // Moved startTime as a local variable
        crawler.adjustThreadPoolSize(10); // Adjust thread pool dynamically

        System.out.println("Starting crawl at: http://books.toscrape.com");
        crawler.crawl("http://books.toscrape.com", 1);
        crawler.shutdown();

        long endTime = System.currentTimeMillis();
        System.out.println("Total URLs visited: " + crawler.totalUrlsVisited);
        System.out.println("Total time taken: " + (endTime - startTime) + " ms");
    }
}
