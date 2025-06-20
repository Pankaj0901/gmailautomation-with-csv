package tests;



import base.BaseTest;
import utils.CSVUtils;
import org.openqa.selenium.By;
import java.util.List;
import java.util.Map;

public class GmailworkflowTest extends BaseTest {

    public void login(String email, String password) throws InterruptedException {
        driver.get("https://mail.google.com/");
        driver.findElement(By.id("identifierId")).sendKeys(email);
        driver.findElement(By.id("identifierNext")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.id("passwordNext")).click();
        Thread.sleep(5000); // Adjust with WebDriverWait in real usage
    }

    public void sendEmail(String to, String subject, String body) throws InterruptedException {
        driver.findElement(By.cssSelector(".T-I.T-I-KE.L3")).click(); // Compose
        Thread.sleep(2000);
        driver.findElement(By.name("to")).sendKeys(to);
        driver.findElement(By.name("subjectbox")).sendKeys(subject);
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(body);
        driver.findElement(By.cssSelector("div[data-tooltip^='Send']")).click();
        Thread.sleep(2000);
    }

    public void performAction(String subject, String action, String reply, String fwd) throws InterruptedException {
        // Search and open email
        driver.findElement(By.name("q")).sendKeys("subject:" + subject + "\n");
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("tr.zA")).click(); // First result

        if (action.equals("reply")) {
            driver.findElement(By.cssSelector("div[aria-label='Reply']")).click();
            Thread.sleep(1000);
            driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(reply);
            driver.findElement(By.cssSelector("div[data-tooltip^='Send']")).click();
        } else if (action.equals("delete")) {
            driver.findElement(By.cssSelector("div[aria-label='Delete']")).click();
        } else if (action.equals("forward")) {
            driver.findElement(By.cssSelector("div[aria-label='More']")).click();
            driver.findElement(By.xpath("//div[text()='Forward']")).click();
            Thread.sleep(1000);
            driver.findElement(By.cssSelector("textarea[name='to']")).sendKeys("yourgmail@gmail.com");
            driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(fwd);
            driver.findElement(By.cssSelector("div[data-tooltip^='Send']")).click();
        }

        Thread.sleep(2000);
    }

    public void logout() throws InterruptedException {
        driver.findElement(By.cssSelector("a[aria-label^='Google Account']")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("Sign out")).click();
        Thread.sleep(3000);
    }

    public void runWorkflow() throws Exception {
        List<Map<String, String>> creds = CSVUtils.readCSV("data/credentials.csv");
        List<Map<String, String>> emails = CSVUtils.readCSV("data/email_data.csv");

        setUp();
        String email = creds.get(0).get("email");
        String password = creds.get(0).get("password");

        login(email, password);

        for (Map<String, String> row : emails) {
            sendEmail(email, row.get("subject"), row.get("body"));
        }

        for (Map<String, String> row : emails) {
            performAction(row.get("subject"), row.get("action"), row.get("reply"), row.get("forward"));
        }

        logout();
        login(email, password); // re-login

        for (Map<String, String> row : emails) {
            if (row.get("action").equals("delete")) {
                // Go to Trash: https://mail.google.com/mail/u/0/#trash
                driver.get("https://mail.google.com/mail/u/0/#trash");
                Thread.sleep(2000);
                // Locate email and delete permanently (manual step unless element is stable)
            }
        }

        logout();
        tearDown();
    }

    public static void main(String[] args) throws Exception {
        new GmailworkflowTest().runWorkflow();
    }
}
