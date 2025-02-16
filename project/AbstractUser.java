abstract class AbstractUser {
    protected String name;
    protected String role;

    public AbstractUser(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public abstract void performSpecialAction();
}