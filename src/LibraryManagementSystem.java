import gui.LoginFrame;
import service.Library;
import service.UserManager;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        UserManager userManager = new UserManager();

        // Launch GUI Login
        new LoginFrame(userManager, library);
    }
}
