package lk.xrontech.watchparadise.model;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String profileUri;
    private String addressNo;
    private String streetName;
    private String city;
    private String zipCode;
    private boolean status = true;

    public User() {
    }

    public User(String userId, String fullName, String email, String profileUri, String addressNo, String streetName, String city, String zipCode, boolean status) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.profileUri = profileUri;
        this.addressNo = addressNo;
        this.streetName = streetName;
        this.city = city;
        this.zipCode = zipCode;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getAddressNo() {
        return addressNo;
    }

    public void setAddressNo(String addressNo) {
        this.addressNo = addressNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
