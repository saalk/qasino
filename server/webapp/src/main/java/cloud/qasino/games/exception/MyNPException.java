package cloud.qasino.games.exception;

// To create a
// - custom unchecked exception (eg, npe), we need to extend the java.lang.RuntimeException class
// - custom checked exception (eg, filenotfound), we have to extend the java.lang.Exception class.
public class MyNPException

        extends RuntimeException {
    public MyNPException(String method, String errorMessage, Throwable err) {
        super("NPE in method [" + method + "] :: " + errorMessage + ", contact the site owner", err);
    }

    public MyNPException(String method, String errorMessage, String s) {
        super("NPE in method [" + method + "] :: " + errorMessage + ", contact the site owner", new NullPointerException("NPE"));
    }

    public MyNPException(String method, String errorMessage) {
        super("NPE in method [" + method + "] :: " + errorMessage + ", contact the site owner", new NullPointerException("NPE"));
    }
}

