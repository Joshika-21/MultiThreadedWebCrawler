package crawler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        // Specify a URL to fetch content from
        String url = "http://books.toscrape.com";  // You can replace this with any URL

        try {
            // Fetch content from the URL and print it using the static method in WebCrawler
            String content = WebCrawler.fetchContent(url);
            System.out.println("Content from the URL:");
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("Error fetching content: " + e.getMessage());
        }

        // Example loop to demonstrate basic functionality (as in your original code)
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}