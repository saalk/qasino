package cloud.qasino.games.exception;

import java.util.InputMismatchException;

// To create a
// - custom unchecked exception (eg, npe), we need to extend the java.lang.RuntimeException class
// - custom checked exception (eg, filenotfound), we have to extend the java.lang.Exception class.
public class MyBusinessException
        extends RuntimeException {

    public MyBusinessException(String errorMessage, Throwable err) {
        super("Business rule violation :: " + errorMessage + ", check the Qasino policy", err);
    }

    public MyBusinessException(String errorMessage) {
        super("Business rule violation :: " + errorMessage + ", check the Qasino policy", new InputMismatchException("IME"));
    }

    public MyBusinessException(String method, String errorMessage) {
        super("Business rule violation in method [" + method + "] :: " + errorMessage + ", check the Qasino policy", new InputMismatchException("IME"));
    }
}

