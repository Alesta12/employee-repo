package org.raghav.employeeService.beans;

import org.bson.codecs.pojo.annotations.BsonId;

public class Employee{
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @BsonId
    private String email;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String phoneNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private  String name;

    public static Employee of(String email,String phoneNumber,String name){
        Employee employee = new Employee();
        employee.name = name;
        employee.email = email;
        employee.phoneNumber = phoneNumber;
        return employee;
    }
}
