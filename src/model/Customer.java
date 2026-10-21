package model;

import java.io.Serializable;

/**
 * Lab 02 – Represents a bank customer with identity and contact details.
 * Lab 03 – Contains a public static nested Address class.
 *           Implements Cloneable to support customer copying.
 * Lab 11 – Implements Serializable.
 */
public class Customer implements Cloneable, Serializable {

    private static final long serialVersionUID = 5L;

    // ------------------------------------------------------------------ id generation

    private static long customerCounter = 100; // starts at 100 → CUST101, CUST102 …

    private static synchronized String generateCustomerId() {
        customerCounter++;
        return "CUST" + customerCounter;
    }

    // ------------------------------------------------------------------ fields

    private final String customerId;

    @model.annotation.MaxLength(value = 50, message = "Name must be at most 50 characters")
    private String name;

    private String email;
    private String mobile;

    private Address address;

    // ------------------------------------------------------------------ constructors

    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name   = name;
        this.email  = email;
        this.mobile = mobile;
    }

    public Customer(String name, String email, String mobile, Address address) {
        this(name, email, mobile);
        this.address = address;
    }

    // ------------------------------------------------------------------ clone (Lab 03)

    /**
     * Returns a shallow copy of this Customer.
     * (Address is shared; a deep clone would copy it too.)
     */
    @Override
    public Customer clone() {
        try {
            return (Customer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloneable is implemented – this should never happen", e);
        }
    }

    // ------------------------------------------------------------------ nested Address class (Lab 03)

    /**
     * Public static nested class – can be used without a Customer instance.
     */
    public static class Address implements Serializable {
        private static final long serialVersionUID = 6L;

        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line    = line;
            this.city    = city;
            this.pincode = pincode;
        }

        public String getLine()    { return line; }
        public String getCity()    { return city; }
        public String getPincode() { return pincode; }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    // ------------------------------------------------------------------ toString / equals

    @Override
    public String toString() {
        return String.format("Customer[%s | %s | %s | %s]",
                customerId, name, email, mobile);
    }

    // ------------------------------------------------------------------ getters

    public String  getCustomerId() { return customerId; }
    public String  getName()       { return name; }
    public String  getEmail()      { return email; }
    public String  getMobile()     { return mobile; }
    public Address getAddress()    { return address; }

    // ------------------------------------------------------------------ setters

    public void setName(String name)       { this.name   = name; }
    public void setEmail(String email)     { this.email  = email; }
    public void setMobile(String mobile)   { this.mobile = mobile; }
    public void setAddress(Address address){ this.address = address; }
}
