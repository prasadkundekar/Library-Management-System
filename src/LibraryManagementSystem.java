import gui.LoginFrame;
import service.Library;
import service.UserManager;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        // Create shared instances of Library and UserManager
        Library library = new Library();
        UserManager userManager = new UserManager();

        // Launch login window
        new LoginFrame(library, userManager);
    }
}
