package cloud.qasino.games.exception;

import java.util.InputMismatchException;

// To create a
// - custom unchecked exception (eg, npe), we need to extend the java.lang.RuntimeException class
// - custom checked exception (eg, filenotfound), we have to extend the java.lang.Exception class.
public class MyBusinessException
        extends RuntimeException {

    public MyBusinessException(String errorMessage, Throwable err) {
        super("MyBusinessException :: " + errorMessage, err);
    }

    public MyBusinessException(String errorMessage) {
        super("MyNPException :: " + errorMessage, new InputMismatchException("IME"));
    }

    public MyBusinessException(String method, String errorMessage) {
        super("MyBusinessException in method [" + method + "] :: " + errorMessage, new InputMismatchException("IME"));
    }
}

