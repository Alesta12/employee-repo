package org.raghav.employeeService.main;

import org.raghav.employeeService.beans.Employee;
import org.raghav.mongoStore.MongoStore;

public class EmployeeService {

    MongoStore<Employee> employeeMongoStore = new MongoStore<>("employeeDB","employee",Employee.class);
    public EmployeeService(){
    }

    public Employee createEmployee(Employee employee){
        try {
            employeeMongoStore.insert(employee);
            return employee;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
