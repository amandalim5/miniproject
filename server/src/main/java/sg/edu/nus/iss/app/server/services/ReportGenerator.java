package sg.edu.nus.iss.app.server.services;

import java.util.Date;
import java.util.TimerTask;

public class ReportGenerator extends TimerTask {

    public ReportGenerator(){

        //Constructor
   
      }

  public void run() {
    System.out.println("Generating report +++++++++++++++++++++++++++++++++++++++++++ here too!");
    System.out.println("Task performed on " + new Date());

  }
}