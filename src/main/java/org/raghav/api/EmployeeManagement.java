// EmployeeManagement.java
package org.raghav.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.raghav.email.EmailClient;
import org.raghav.employeeService.beans.Employee;
import org.raghav.employeeService.main.EmployeeService;
import org.raghav.otpService.OtpClient;
import org.raghav.redis.ObjectRedisClient;
import org.raghav.redis.RedisConfig;
import org.raghav.redis.StringRedisClient;

@Path("/employee-management")
@CrossOrigin(origins = "*")
public class EmployeeManagement {

    private final EmailClient emailClient;
    private final OtpClient otpClient;
    private final EmployeeService employeeService;

    public EmployeeManagement() {
        RedisConfig config = new RedisConfig();
        ObjectRedisClient<Employee> objClient = new ObjectRedisClient<>(config, Employee.class);
        StringRedisClient strClient = new StringRedisClient(config);
        this.employeeService =  new EmployeeService();
        this.otpClient = new OtpClient(objClient, strClient);
        this.emailClient = new EmailClient(otpClient, objClient, strClient);
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Employee employee) {
        try {
            emailClient.sendOtp(employee);
            return Response.ok("OTP sent successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to send OTP: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/authenticate-otp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateOtp(@QueryParam("otp") String otp, @QueryParam("email") String email) {
        try {
            Employee employee  = otpClient.authenticateOtp(otp, email);
            if(employee != null){
                employeeService.createEmployee(employee);
            }
            return Response.ok("Authenticated").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to authenticate: " + e.getMessage())
                    .build();
        }
    }
}
