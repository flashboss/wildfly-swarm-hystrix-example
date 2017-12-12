package org.wildfly.swarm.employee.api;

import static java.lang.Math.random;
import static java.lang.Thread.sleep;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.wildfly.swarm.employee.model.Employee;

@ApplicationScoped
@Path("/employees")
public class EmployeeController {
	@GET
	@Produces(APPLICATION_JSON)
	public List<Employee> findAll() {
		if (true) {
	        /* fail 20% of times */
	        if (random() > 0.8) {
	            throw new RuntimeException("random failure loading order over network");
	        }

	        /* latency spike 50% of the time */
	        if (random() > 0.5) {
	            // random latency spike
	            try {
	                sleep((int) (random() * 2000) + 25);
	            } catch (InterruptedException e) {
	                // do nothing
	            }
	        }
		}
		
		return new ArrayList<Employee>() {
			
			private static final long serialVersionUID = 8003977432905504763L;

			{
				add(new Employee(1L, "John"));
				add(new Employee(2L, "Sarah"));
				add(new Employee(3L, "Matt"));
				add(new Employee(4L, "Linda"));
			}
		};
	}
}
