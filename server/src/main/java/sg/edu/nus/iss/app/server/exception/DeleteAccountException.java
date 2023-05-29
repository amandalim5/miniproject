package sg.edu.nus.iss.app.server.exception;

public class DeleteAccountException extends Exception{
    public DeleteAccountException(){
        super();
    }

    public DeleteAccountException(String msg){
        super(msg);
    }
}
