package sg.edu.nus.iss.app.server;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.edu.nus.iss.app.server.services.ReportGenerator;


@SpringBootApplication
public class ServerApplication {



	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		System.out.println("this is a test of the main function ++++++++++++++++++++++++++++++++++++++++++++  here!");
		
    	Calendar calendar = Calendar.getInstance();
		// calendar.set(
		// 	Calendar.DAY_OF_WEEK,
		// 	Calendar.THURSDAY
		//   );
		// mistakes you have made: you used SG time, not UTC 
		// every day, 5:30 pm, SGT, the number of checks and likes should reset.
    	calendar.set(Calendar.HOUR_OF_DAY, 10);
    	calendar.set(Calendar.MINUTE, 38);
    	calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date d = calendar.getTime();
		System.out.println(d.toString() + " what is the date>>>>");

		Timer timer = new Timer();
		timer.schedule(
      		new ReportGenerator(),
      		calendar.getTime(),
			// 0,
      		1000 * 60 * 60 * 24
    	);
	}

}
