package catan.services;

public interface ManagementService {
    void startAutomatePlayerLifeCycle(String gameId, String userName, String botName);
    void stopAutomatePlayerLifeCycle(String gameId, String userName);
}
