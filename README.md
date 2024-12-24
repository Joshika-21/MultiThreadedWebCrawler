# MultiThreadedWebCrawler
# Multi-Threaded Web Crawler

This project implements a multi-threaded web crawler in Java. It crawls web pages starting from a given URL, extracts links, and recursively visits them up to a defined depth. The program supports adjustable thread pool sizes, URL persistence for resuming interrupted crawls, and robust error handling.

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or later.
- IntelliJ IDEA or any Java-supported IDE.
- jsoup library for HTML parsing (already integrated).

### Installation
1. Clone the repository or download the project files.
2. Import the project into an IDE like IntelliJ IDEA or Eclipse.
3. Add the jsoup library to the project:
    - Download jsoup from https://jsoup.org/download.
    - Add the .jar file to your project's libraries in the IDE.

### Running the Project
#### 1. Modify Main.java:
- Update the starting URL and other configurations as needed:
   - String url = https://books.toscrape.com;
   - WebCrawler crawler = new WebCrawler(5, 5);
#### 2. Run the Program:
- Execute the main method in Main.java
#### 3. Expected Output:
- Add the jsoup library to the project:
   - Total URLs visited.
   - Total time taken for crawling.
   - Any failed URLs that could not be fetched.

## How to Run the Code

1. Open the project in your IDE.
2. Ensure the Jsoup library is correctly added to the classpath.
3. Navigate to the Main.java file.
4. Update the initial configurations (e.g., starting URL, thread count, max depth).
5. Execute the program by running the main method.

## Code Structure

- WebCrawler: The main class that manages the overall crawling process, including thread management, URL tracking, and persistence.
- CrawlTask: A nested class within WebCrawler that implements Runnable and handles the logic for crawling each URL in a separate thread.

#### Key methods:
- fetchContent(String url): Fetches HTML content from a URL.
- extractLinks(String htmlContent): Extracts all links from the fetched HTML content.
- saveVisitedUrls(): Saves the list of visited URLs to a file for resumption.
- loadVisitedUrls(): Loads previously visited URLs from a file.
- adjustThreadPoolSize(int newThreadCount): Dynamically adjusts the number of threads during execution.

## Features

- Multi-threading: Uses Java's ExecutorService to crawl multiple URLs concurrently.
- URL Persistence: Saves visited URLs to a file to allow resumption after interruptions.
- Adjustable Thread Pool Size: Dynamically adjusts the number of threads during execution.
- Error Handling: Retires failed URLs and logs permanently failed URLs.
- Crawl Statistics: Tracks the number of visited URLs and total execution time.

## Enhancements
#### Suggested Future Features:
1. Customizable Crawl Policies:
   - Add domain filtering or URL pattern filtering to restrict the scope of crawling.
2. Politeness Policy:
   - Introduce delays between requests to avoid server overload.
3. Crawl Strategy:
   - Implement options for depth-first or breadth-first crawling.

## Example Output

```yaml
Visited URLs loaded from file.
Thread pool size adjusted to: 10
Starting crawl at: http://books.toscrape.com
Total URLs visited so far: 1
Thread pool-2-thread-1 crawling: http://books.toscrape.com at depth 2
Total URLs visited so far: 2
Failed to fetch or parse URL: http://books.toscrape.com
Retrying URL: http://books.toscrape.com
Executor is shutting down. Skipping retry for URL: http://books.toscrape.com
Visited URLs saved to file.
Total URLs visited: 2
Total time taken: 477 ms

Process finished with exit code 0
