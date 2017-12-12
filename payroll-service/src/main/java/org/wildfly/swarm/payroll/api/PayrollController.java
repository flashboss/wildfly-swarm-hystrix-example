package org.wildfly.swarm.payroll.api;

import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;
import static com.netflix.hystrix.HystrixCommandProperties.Setter;
import static java.util.logging.Logger.getLogger;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.wildfly.swarm.payroll.api.Utils.getEmployeeEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.wildfly.swarm.payroll.model.Employee;
import org.wildfly.swarm.payroll.model.Payroll;

import com.netflix.hystrix.HystrixCommand;

@ApplicationScoped
@Path("/payroll")
public class PayrollController {
	private static Logger LOG = getLogger(PayrollController.class.getName());

	public PayrollController() {
		Setter().withCircuitBreakerRequestVolumeThreshold(10);
	}

	@GET
	@Produces(APPLICATION_JSON)
	public List<Payroll> findAll() {
		List<Employee> employees = new FindEmployeesCommand().execute();
		List<Payroll> payroll = new ArrayList<>();
		for (Employee employee : employees) {
			payroll.add(new Payroll(employee, employee.getId() * 1500));
		}

		return payroll;
	}

	class FindEmployeesCommand extends HystrixCommand<List<Employee>> {
		public FindEmployeesCommand() {
			super(asKey("EmployeesGroup"));
		}

		@Override
		protected List<Employee> run() {
			String url = getEmployeeEndpoint("/employees");
			Builder request = newClient().target(url).request();
			try {
				return request.get(new GenericType<List<Employee>>() {
				});
			} catch (Exception e) {
				LOG.severe("Failed to call Employee service at " + url + ": " + e.getMessage());
				throw e;
			}
		}
	}
}
