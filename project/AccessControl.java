interface AccessControl {
    void grantAccess(String id, String location);
    String getAccess(String id);
}