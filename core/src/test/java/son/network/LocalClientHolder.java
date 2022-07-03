package son.network;

public class LocalClientHolder implements ClientHolder {

    @Override
    public void start() {
        // Just don't do anything        
    }

    @Override
    public String getClient() {
        return "localhost";
    }
    
}
