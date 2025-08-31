package org.raghav.otpService;

import org.raghav.employeeService.beans.Employee;
import org.raghav.redis.ObjectRedisClient;
import org.raghav.redis.StringRedisClient;

import java.util.UUID;

public class OtpClient {

    private final ObjectRedisClient<Employee> employeeObjectRedisClient;
    private final StringRedisClient stringRedisClient;

    public OtpClient(ObjectRedisClient<Employee> employeeObjectRedisClient, StringRedisClient stringRedisClient){
        this.employeeObjectRedisClient = employeeObjectRedisClient;
        this.stringRedisClient = stringRedisClient;
    }

    public String getOtp() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

    public Employee authenticateOtp(String otp, String userEmail) {
        String storedOtp = stringRedisClient.get("otp_" + userEmail);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Failed to authenticate: invalid OTP");
        }

        Employee employee = employeeObjectRedisClient.get(userEmail);
        if (employee != null) {
            System.out.println("Authenticated employee email:");
        } else {
            System.out.println("No employee found for given email");
        }
        return employee;
    }
}
