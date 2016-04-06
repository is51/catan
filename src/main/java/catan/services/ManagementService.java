package catan.services;

public interface ManagementService {
    void startAutomatePlayerLifeCycle(String secretKey,String gameId,String userName);
    void stopAutomatePlayerLifeCycle(String secretKey,String gameId,String userName);
}
